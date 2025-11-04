package graduation.project.DoDutch_server.domain.trip.service;

import graduation.project.DoDutch_server.domain.member.entity.Member;
import graduation.project.DoDutch_server.domain.member.entity.Role;
import graduation.project.DoDutch_server.domain.member.repository.MemberRepository;
import graduation.project.DoDutch_server.domain.trip.dto.Request.FeatureDto;
import graduation.project.DoDutch_server.domain.trip.dto.Request.PredictRequestDto;
import graduation.project.DoDutch_server.domain.trip.converter.TripMemberConverter;
import graduation.project.DoDutch_server.domain.trip.dto.Request.TripJoinRequestDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Request.TripRequestDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Request.*;
import graduation.project.DoDutch_server.domain.trip.dto.Response.ChatGPTResponseDto;
import graduation.project.DoDutch_server.domain.trip.dto.Request.TripUpdateRequestDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripDetailResponseDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripResponseDTO;
import graduation.project.DoDutch_server.domain.trip.converter.TripConverter;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripSuggestionResponseDto;
import graduation.project.DoDutch_server.domain.trip.repository.TripMemberRepository;
import graduation.project.DoDutch_server.domain.trip.repository.TripRepository;
import graduation.project.DoDutch_server.domain.trip.entity.Trip;
import graduation.project.DoDutch_server.domain.trip.entity.TripMember;
import graduation.project.DoDutch_server.global.common.apiPayload.code.status.ErrorStatus;
import graduation.project.DoDutch_server.global.common.exception.handler.ErrorHandler;
import graduation.project.DoDutch_server.global.config.openai.OpenAiConfig;
import graduation.project.DoDutch_server.global.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Period;
import java.util.*;

@RequiredArgsConstructor
@Service
public class TripServiceImpl implements TripService{
    private final TripRepository tripRepository;
    private final MemberRepository memberRepository;
    private final TripMemberRepository tripMemberRepository;
    private final OpenAiConfig openAiConfig;

    @Value("${openai.model}")
    private String model;
    @Value("${openai.api.url}")
    private String apiUrl;
    private final AuthUtils authUtils;

    /*
    여행 생성
     */
    @Transactional
    @Override
    public Long createTrip(TripRequestDTO tripRequestDTO) throws IOException {
        //Todo: UUID를 통해 랜덤 값을 생성해 Uuid 객체에 저장
        //Todo: 랜덤 값을 s3 업로드 함수의 키값으로 이용해 실제 tripImageUrl을 생성
        //Todo: 만들어진 진짜 tripImageUrl을 toEntity의 매개변수로 넣어준다.

        Member currentMember = authUtils.getCurrentMember();

        //UUID를 통해 랜덤한 참여 코드 12자리를 생성한다.
        String joinCode = UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        //이미지를 로컬에 저장후 경로 반환
        String savedPath = saveImageToLocal(tripRequestDTO.getTripImage());

        //여행을 저장한다.
        Trip savedTrip = tripRepository.save(TripConverter.toEntity(tripRequestDTO, joinCode, savedPath));

        //여행 생성한 회원을 여행 참여자로 저장한다.
        tripMemberRepository.save(TripMemberConverter.toEntity(currentMember, savedTrip));

        return savedTrip.getId();
    }

    /*
    이미지 저장 및 경로 반환
     */
    private String saveImageToLocal(MultipartFile file) throws IOException {
        //저장할 이미지 경로 생성
        String uploadDir = "C:/Users/lee07/Desktop/upload/";
//        String uploadDir = "C:/Users/kimhy/Desktop/Backend/uploads/";
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        //디렉토리가 없으면 생성
        Files.createDirectories(Path.of(uploadDir));
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
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
        return TripConverter.toDetailDto(trip);
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
        tripRepository.deleteById(tripId);
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
    public List<Float> predictBudget(PredictRequestDto requestDto) {

        Long userId = AuthUtils.getCurrentMemberId();
        isPremiumMember(userId);

        FeatureDto featureDto = new FeatureDto();

        Long numCompanion = requestDto.numCompanions();
        featureDto.setFeature("NUM_COMPANIONS", (float)numCompanion);

        int month = requestDto.startDate().getMonthValue();
        featureDto.setFeature("MONTH", (float) month);

        Period period = Period.between(requestDto.startDate(), requestDto.endDate());
        int days = period.getDays();
        if (days == 0) featureDto.setFeature("DURATION_CATEGORY_당일", 1f);
        else if (days == 1) {
            featureDto.setFeature("DURATION_CATEGORY_1박 2일", 1f);
        }
        else if (days == 2) {
            featureDto.setFeature("DURATION_CATEGORY_2박 3일", 1f);
        }
        else featureDto.setFeature("DURATION_CATEGORY_3박 4일 이상", 1f);

        String place = requestDto.place().toString();
        featureDto.setFeature("LOCATION_"+ place, 1f);

        System.out.println("DAYS: "+days);
        System.out.println("NUM_COMPANIONS: "+numCompanion);
        System.out.println("MONTH: "+month);
        System.out.println("LOCATION: "+place);
        System.out.println("IS_HOLIDAY: False");
        System.out.println(featureDto.toValueList());


        //Todo 외부 api와 연동하여 휴일 여부 받아오기.
        featureDto.setFeature("IS_HOLIDAY", 0f);

        return featureDto.toValueList();
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
        isPremiumMember(userId);

        RestTemplate restTemplate = openAiConfig.restTemplate();

        String prompt = requestDto.place() + "/" + requestDto.endDate().toString() + "~" + requestDto.startDate().toString() + "에 맞는 날짜 별 여행지를 계획해서 알려줘.";
        ChatGPTRequestDto chatGPTRequestDto = ChatGPTRequestDto.gptRequest(model, prompt);

        ChatGPTResponseDto chatGPTResponseDto = restTemplate.postForObject(apiUrl, chatGPTRequestDto, ChatGPTResponseDto.class);
        return new TripSuggestionResponseDto(chatGPTResponseDto.choices().get(0).message().content());
    }
}
