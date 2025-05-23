package graduation.project.DoDutch_server.domain.trip.service;

import graduation.project.DoDutch_server.domain.member.entity.Member;
import graduation.project.DoDutch_server.domain.member.repository.MemberRepository;
import graduation.project.DoDutch_server.domain.trip.dto.Request.TripJoinRequestDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Request.TripRequestDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripDetailResponseDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripResponseDTO;
import graduation.project.DoDutch_server.domain.trip.dto.TripConverter;
import graduation.project.DoDutch_server.domain.trip.repository.TripMemberRepository;
import graduation.project.DoDutch_server.domain.trip.repository.TripRepository;
import graduation.project.DoDutch_server.domain.trip.entity.Trip;
import graduation.project.DoDutch_server.domain.trip.entity.TripMember;
import graduation.project.DoDutch_server.global.common.apiPayload.code.status.ErrorStatus;
import graduation.project.DoDutch_server.global.common.exception.handler.ErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
public class TripServiceImpl implements TripService{
    private final TripRepository tripRepository;
    private final MemberRepository memberRepository;
    private final TripMemberRepository tripMemberRepository;

    /*
    여행 생성
     */
    @Transactional
    @Override
    public Long createTrip(TripRequestDTO tripRequestDTO, Long memberId) {
        //Todo: UUID를 통해 랜덤 값을 생성해 Uuid 객체에 저장
        //Todo: 랜덤 값을 s3 업로드 함수의 키값으로 이용해 실제 tripImageUrl을 생성
        //Todo: 만들어진 진짜 tripImageUrl을 toEntity의 매개변수로 넣어준다.

        //UUID를 통해 랜덤한 참여 코드 12자리를 생성한다.
        String joinCode = UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        //여행을 저장한다.
        Trip savedTrip = tripRepository.save(TripConverter.toEntity(tripRequestDTO, joinCode));

        //여행 생성한 회원을 여행 참여자로 저장한다.
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isEmpty()) throw new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND);

        TripMember tripMember = TripMember.builder()
                .member(optionalMember.get())
                .trip(savedTrip)
                .build();
        tripMemberRepository.save(tripMember);

        return savedTrip.getId();
    }

    /*
    여행 참여
     */
    @Transactional
    @Override
    public void joinTrip(TripJoinRequestDTO tripJoinRequestDTO, Long memberId) {
        //joinCode를 이용하여 여행을 불러온다.
        Optional<Trip> optionalTrip = tripRepository.findByJoinCode(tripJoinRequestDTO.getJoinCode());
        if (optionalTrip.isEmpty()) throw new ErrorHandler(ErrorStatus.TRIP_NOT_EXIST);

        //memberId를 이용하여 회원을 불러온다.
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isEmpty()) throw new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND);

        Member member = optionalMember.get();
        Trip trip = optionalTrip.get();
        List<TripMember> tripMemberList = trip.getTripMembers();

        //회원을 여행의 참여자로 저장하기 전 이미 참여가된 사람인지 검사한다.
        for (TripMember tripMember : tripMemberList) {
            if (member.equals(tripMember.getMember())) throw new ErrorHandler(ErrorStatus.TRIP_MEMBER_EXIST);
        }

        //tripMember에 저장
        TripMember tripMember = TripMember.builder().trip(trip).member(member).build();
        tripMemberRepository.save(tripMember);
        return;
    }

    /*
    여행 공유시 정보 조회
     */
    @Transactional
    @Override
    public TripResponseDTO shareTrip(Long tripId) {
        Optional<Trip> optionalTrip = tripRepository.findById(tripId);
        if (optionalTrip.isEmpty()) throw new ErrorHandler(ErrorStatus.TRIP_NOT_EXIST);
        return TripConverter.toDto(optionalTrip.get());
    }

    /*
    여행 목록 전체 조회
     */
    @Override
    @Transactional
    public List<TripDetailResponseDTO> searchTrip(String keyWord) {

        String clearKeyword = keyWord.trim(); // 좌우 공백 삭제

        //여행 이름에 clearKeyword가 포함되는 것들 갖고 오기.
        List<Trip> byTripName = Optional.ofNullable(tripRepository.findByNameLike("%"+ clearKeyword +"%"))
                .orElse(Collections.emptyList());

        Set<Trip> tripSet = new HashSet<>(byTripName); // 여행 목록의 중복을 방지하기 위해 Set<> 에다 저장.

        //멤버 이름으로 검색
        List<TripMember> tripMemberList = tripMemberRepository.findAll(); // 나의 모든 여행 친구 목록 불러오기.
        for (TripMember tripMember : tripMemberList){ // 여행 친구의 이름이 clearKeyword와 같은 것들을 Set<> 에 추가한다.
            if (Objects.equals(tripMember.getMember().getName(), clearKeyword)){
                tripSet.add(tripMember.getTrip());
            }
        }

        //연도로 검색
        boolean isNumeric = clearKeyword.matches("\\d+");
        if (isNumeric) { // 만약 clearKeyword가 숫자이면 연도로 검색한 결과를 Set<> 에 추가한다.
            Integer year = Integer.parseInt(clearKeyword);
            List<Trip> byYear = Optional.ofNullable(tripRepository.findByYear(year))
                    .orElse(Collections.emptyList());
            tripSet.addAll(byYear);
        }

        //Set<> 을 List<>로 바꾼 후 리스트가 비어있음 에러를 호출한다.
        List<Trip> trips = new ArrayList<>(tripSet);
        if (trips.isEmpty()) throw new ErrorHandler(ErrorStatus.TRIP_NOT_EXIST);

        return TripConverter.toDetailListDto(trips);

    }

    /*
    여행 정보 조회
     */
    @Override
    @Transactional
    public TripDetailResponseDTO detailTrip(Long tripId) {
        Optional<Trip> optionalTrip = tripRepository.findById(tripId);
        if (optionalTrip.isEmpty()) throw new ErrorHandler(ErrorStatus.TRIP_NOT_EXIST);
        return TripConverter.toDetailDto(optionalTrip.get());
    }
}
