package graduation.project.DoDutch_server.domain.trip.service;

import graduation.project.DoDutch_server.domain.expense.entity.Expense;
import graduation.project.DoDutch_server.domain.member.entity.Member;
import graduation.project.DoDutch_server.domain.member.entity.Role;
import graduation.project.DoDutch_server.domain.member.repository.MemberRepository;
import graduation.project.DoDutch_server.domain.photo.entity.Photo;
import graduation.project.DoDutch_server.domain.photo.repository.PhotoRepository;
import graduation.project.DoDutch_server.domain.trip.dto.Request.FeatureDto;
import graduation.project.DoDutch_server.domain.trip.dto.Request.PredictRequestDto;
import graduation.project.DoDutch_server.domain.trip.converter.TripMemberConverter;
import graduation.project.DoDutch_server.domain.trip.dto.Request.TripJoinRequestDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Request.TripRequestDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Request.*;
import graduation.project.DoDutch_server.domain.trip.dto.Response.*;
import graduation.project.DoDutch_server.domain.trip.dto.Request.TripUpdateRequestDTO;
import graduation.project.DoDutch_server.domain.trip.converter.TripConverter;
import graduation.project.DoDutch_server.domain.trip.repository.TripMemberRepository;
import graduation.project.DoDutch_server.domain.trip.repository.TripRepository;
import graduation.project.DoDutch_server.domain.trip.entity.Trip;
import graduation.project.DoDutch_server.domain.trip.entity.TripMember;
import graduation.project.DoDutch_server.global.common.apiPayload.code.status.ErrorStatus;
import graduation.project.DoDutch_server.global.common.exception.handler.ErrorHandler;
import graduation.project.DoDutch_server.global.config.aws.S3PathManager;
import graduation.project.DoDutch_server.global.config.aws.S3Manager;
import graduation.project.DoDutch_server.global.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RequiredArgsConstructor
@Service
public class TripServiceImpl implements TripService{
    private final TripRepository tripRepository;
    private final MemberRepository memberRepository;
    private final TripMemberRepository tripMemberRepository;
    private final PhotoRepository photoRepository;
    private final RestTemplate template;

    @Value("${openai.model}")
    private String model;
    @Value("${openai.api.url}")
    private String apiUrl;
    @Value("${flask.url}")
    private String flaskUrl;

    private final AuthUtils authUtils;
    private final S3PathManager s3PathManager;
    private final S3Manager s3Manager;

    /*
    여행 생성
     */
    @Transactional
    @Override
    public Long createTrip(TripRequestDTO tripRequestDTO) {

        Member currentMember = authUtils.getCurrentMember();
        if (tripRequestDTO.getStartDate().isAfter(tripRequestDTO.getEndDate())) {
            throw new ErrorHandler(ErrorStatus.TRIP_CREATE_FAIL);
        }

        //UUID를 통해 랜덤한 참여 코드 12자리를 생성한다.
        String joinCode = UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        String savedPath = !tripRequestDTO.getTripImage().isEmpty() ? saveImageToS3(tripRequestDTO.getTripImage()) : null;

        //여행을 저장한다.
        Trip savedTrip = tripRepository.save(TripConverter.toEntity(tripRequestDTO, joinCode, savedPath));

        //여행 생성한 회원을 여행 참여자로 저장한다.
        tripMemberRepository.save(TripMemberConverter.toEntity(currentMember, savedTrip));

        return savedTrip.getId();
    }

    /*
    이미지 저장 및 경로 반환
     */
    private String saveImageToS3(MultipartFile file) {
        String uuid = UUID.randomUUID().toString();
        //저장할 이미지 경로 생성
        String keyName = s3PathManager.generateKeyName(
                s3PathManager.getTripMain(),
                file,
                uuid
        );

        return s3Manager.upload(file, keyName);
    }

    /*
    여행 참여
     */
    @Transactional
    @Override
    public void joinTrip(TripJoinRequestDTO tripJoinRequestDTO) {
        Member currentMember = authUtils.getCurrentMember();

        //joinCode를 이용하여 여행을 불러온다.
        Trip trip = tripRepository.findByJoinCode(tripJoinRequestDTO.getJoinCode())
                .orElseThrow(()->new ErrorHandler(ErrorStatus.TRIP_NOT_EXIST));

        //회원을 여행의 참여자로 저장하기 전 이미 참여가된 사람인지 검사한다.
        List<TripMember> tripMemberList = trip.getTripMembers();
        for (TripMember tripMember : tripMemberList) {
            if (currentMember.equals(tripMember.getMember()))
                throw new ErrorHandler(ErrorStatus.TRIP_MEMBER_EXIST);
        }

        //tripMember에 저장
        TripMember tripMember = TripMemberConverter.toEntity(currentMember, trip);
        tripMemberRepository.save(tripMember);
    }

    /*
    여행 공유시 정보 조회
     */
    @Transactional
    @Override
    public TripResponseDTO shareTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(()->new ErrorHandler(ErrorStatus.TRIP_NOT_EXIST));
        return TripConverter.toDto(trip);
    }

    /*
    여행 목록 전체 조회
     */
    @Override
    @Transactional
    public List<TripDetailResponseDTO> searchTrip(String keyWord) {
        Member currentMember = authUtils.getCurrentMember();
        Long memberId = currentMember.getId();

        List<Trip> myTrip = tripMemberRepository.findByMemberId(memberId)
                .stream()
                .map(TripMember::getTrip)
                .toList();

        if (keyWord == null || keyWord.isEmpty())
            return TripConverter.toDetailListDto(myTrip);

        String clearKeyword = keyWord.trim(); // 좌우 공백 삭제
        Set<Trip> tripSet = new HashSet<>(validateTripByTripName(clearKeyword, myTrip));
        tripSet.addAll(validateTripByYear(clearKeyword,myTrip));
        tripSet.addAll(validateTripByMemberName(clearKeyword, myTrip));

        //Set<> 을 List<>로 바꾼 후 리스트가 비어있음 에러를 호출한다.
        List<Trip> trips = new ArrayList<>(tripSet);
        if (trips.isEmpty()) throw new ErrorHandler(ErrorStatus.TRIP_NOT_FOUND);

        return TripConverter.toDetailListDto(trips);

    }

    // 여행 명으로 검색
    private Set<Trip> validateTripByTripName(String keyword, List<Trip> trips) {
        Set<Trip> tripSet = new HashSet<>();
        for (Trip trip : trips) {
            if (trip.getName().contains(keyword)) {
                tripSet.add(trip);
            }
        }
        return tripSet;
    }

    // 연도로 검색
    private Set<Trip> validateTripByYear(String keyword, List<Trip> trips) {
        Set<Trip> tripSet = new HashSet<>();
        if (!keyword.matches("\\d+")) // keyword가 숫자가 아니면 종료
            return tripSet;

        int year = Integer.parseInt(keyword);
        for (Trip trip : trips) {
            if (trip.getStartDate().getYear() == year
                    || trip.getEndDate().getYear() == year) {
                tripSet.add(trip);
            }
        }
        return tripSet;
    }

    // 멤버 이름으로 검색
    private Set<Trip> validateTripByMemberName(String keyword, List<Trip> trips) {
        Set<Trip> tripSet = new HashSet<>();

        for (Trip trip : trips) {
            List<TripMember> tripMemberList = trip.getTripMembers();
            for (TripMember tripMember : tripMemberList) {
                if (tripMember.getMember() == null) continue;
                String name = tripMember.getMember().getName();
                if (name == null || name.isEmpty()) continue; // 멤버 이름이 비어있으면 넘어간다.
                if (name.contains(keyword)) {
                    tripSet.add(trip);
                }
            }
        }
        return tripSet;
    }

    /*
    여행 정보 조회
     */
    @Override
    @Transactional
    public TripDetailResponseDTO detailTrip(Long tripId) {
        Member member = authUtils.getCurrentMember();
        tripMemberRepository
                .findByTripIdAndMemberId(tripId, member.getId())
                .orElseThrow(()->new ErrorHandler((ErrorStatus.TRIP_NOT_EXIST)));

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(()->new ErrorHandler(ErrorStatus.TRIP_NOT_EXIST));
        return TripConverter.toDetailDto(trip, photoRepository);
    }

    /*
    여행 수정
     */
    @Override
    @Transactional
    public void updateTrip(
            Long tripId, TripUpdateRequestDTO requestDTO
    ){
        Member currentMember = authUtils.getCurrentMember();
        tripMemberRepository
                .findByTripIdAndMemberId(tripId, currentMember.getId())
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.TRIP_NOT_EXIST));

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.TRIP_NOT_EXIST));

        trip.updateInfo(requestDTO);

        if (requestDTO.getTripImage() != null && !requestDTO.getTripImage().isEmpty()) {
            if (trip.getTripImageUrl() != null) {
                deleteTripImage(trip.getTripImageUrl());
            }

            String savedPath = saveImageToS3(requestDTO.getTripImage());
            trip.setTripImageUrl(savedPath);
        }

    }

    /*
    여행 삭제
     */
    @Override
    @Transactional
    public void deleteTrip(Long tripId) {
        Member currentMember = authUtils.getCurrentMember();
        tripMemberRepository
                .findByTripIdAndMemberId(tripId, currentMember.getId())
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.TRIP_NOT_EXIST));

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.TRIP_NOT_EXIST));
        if (trip.getTripImageUrl() != null)
            deleteTripImage(trip.getTripImageUrl());

        List<Expense> expenses = trip.getExpenses();
        for (Expense expense : expenses) {
            List<Photo> photos = photoRepository.findByExpenseId(expense.getId());
            for (Photo photo : photos) {
                if (photo.getPhotoUrl() != null){
                    deleteExpenseImage(photo.getPhotoUrl());
                }
            }
        }

        tripRepository.deleteById(tripId);
    }

    private void deleteTripImage(String imageUrl) {
        String keyName = s3PathManager.deleteKeyName(
                s3PathManager.getTripMain(),
                imageUrl
        );
        s3Manager.delete(keyName);
    }

    private void deleteExpenseImage(String imageUrl) {
        String keyName = s3PathManager.deleteKeyName(
                s3PathManager.getExpenseMain(),
                imageUrl
        );
        s3Manager.delete(keyName);
    }

    private void isPremiumMember(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        if (!member.getRole().equals(Role.PREMIUM))
            throw new ErrorHandler(ErrorStatus._FORBIDDEN);
    }

    /*
    여행 경비 예측
     */
    @Override
    @Transactional
    public PredictResponseDto predictBudget(PredictRequestDto requestDto) {

        Long userId = AuthUtils.getCurrentMemberId();
//        isPremiumMember(userId);

        List<Float> features = new FeatureDto().setFeatures(requestDto);

        Map<String,Object> body = Map.of("features", features);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String,Object>> entity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, entity, Map.class);
        Double predict = (Double) response.getBody().get("predicted_total_cost");
        return new PredictResponseDto(predict.intValue()+"원");
    }

    /*
    gpt 여행지 추천
     */
    @Override
    @Transactional
    public TripSuggestionResponseDto recommendTrip(
            TripSuggestionRequestDto requestDto
    ){
        Long userId = AuthUtils.getCurrentMemberId();
//        isPremiumMember(userId);

        String prompt = requestDto.place() +
                "에서" +
                requestDto.startDate().toString() +
                "~" + requestDto.endDate().toString() +
                "동안 날짜별 아침, 점심, 저녁 여행지를 간략하게 해서 2가지 계획안을 최대한 매우매우 정말 빠르게 작성하여 응답하라." +
                "이때 방문하는 장소는" +
                requestDto.place() +
                "에 실제 존재하는 장소로 유명 음식점, 카페의 이름 또는 관광 명소의 이름을 제시한다." +
                "모든 답변은 불필요한 문장 없이 바로 정답 또는 코드만 제시한다." +
                "감탄사, 문장부호 이모티콘, 자연스러운 대화체 표현을 포함하지 않는다." +
                "후속 제안, 추가 설명, 맺음말 없이 끝낸다." +
                "답변 형식: [1안: ], [2안: ]." +
                "ex: [1안: 몇 월 며칠 아침: ooo, 점심: ooo, ...], [" ;

        ChatGPTRequestDto chatGPTRequestDto = ChatGPTRequestDto.gptRequest(model, prompt);

        ChatGPTResponseDto chatGPTResponseDto = template.postForObject(apiUrl, chatGPTRequestDto, ChatGPTResponseDto.class);
        return new TripSuggestionResponseDto(chatGPTResponseDto.choices().get(0).message().content());
    }

    @Transactional
    public void deleteMember(Long memberId){
        List<TripMember> tripMembers = tripMemberRepository.findByMemberId(memberId);
        for (TripMember tripMember : tripMembers) {
            tripMember.setMember(null);
        }
    }
}
