package graduation.project.DoDutch_server.domain.trip.Service;

import graduation.project.DoDutch_server.domain.member.entity.Member;
import graduation.project.DoDutch_server.domain.member.repository.MemberRepository;
import graduation.project.DoDutch_server.domain.trip.DTO.Request.TripJoinRequestDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.Request.TripRequestDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.Response.TripDetailResponseDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.Response.TripResponseDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.TripConverter;
import graduation.project.DoDutch_server.domain.trip.Repository.TripMemberRepository;
import graduation.project.DoDutch_server.domain.trip.Repository.TripRepository;
import graduation.project.DoDutch_server.domain.trip.entity.Trip;
import graduation.project.DoDutch_server.domain.trip.entity.TripMember;
import graduation.project.DoDutch_server.global.common.apiPayload.code.status.ErrorStatus;
import graduation.project.DoDutch_server.global.common.exception.handler.ErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    public Long createTrip(TripRequestDTO tripRequestDTO) {
        Trip savedTrip = tripRepository.save(TripConverter.toEntity(tripRequestDTO));
        return savedTrip.getId();
    }

    /*
    여행 참여
     */
    @Transactional
    @Override
    public Void joinTrip(TripJoinRequestDTO tripJoinRequestDTO, Long memberId) {
        //trip과 member 불러오기
        Optional<Trip> optionalTrip = tripRepository.findByJoinCode(tripJoinRequestDTO.getJoinCode());
        if (optionalTrip.isEmpty()) throw new ErrorHandler(ErrorStatus.TRIP_NOT_EXIST);

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isEmpty()) throw new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND);

        Member member = optionalMember.get();
        Trip trip = optionalTrip.get();
        List<TripMember> tripMemberList = trip.getTripMembers();

        //중복된 tripMember 있는지 검사.
        for (TripMember tripMember : tripMemberList) {
            if (member.equals(tripMember.getMember())) throw new ErrorHandler(ErrorStatus.TRIP_MEMBER_EXIST);
        }

        //tripMember 저장
        tripMemberRepository.save(TripMember.builder().trip(trip).member(member).build());
        return null;
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

        //여행 이름이 keyword와 같은 것들 갖고 오기.
        List<Trip> byTripName = Optional.ofNullable(tripRepository.findByNameLike("%"+ keyWord +"%"))
                .orElse(Collections.emptyList());
        Set<Trip> tripSet = new HashSet<>(byTripName);

        //멤버 이름 검색
        List<Trip> byMemberName = new ArrayList<>();
        List<TripMember> tripMemberList = tripMemberRepository.findAll();
        for (TripMember tripMember : tripMemberList){
            Member member = tripMember.getMember();
            if (Objects.equals(member.getName(), keyWord)){
                byMemberName.add(tripMember.getTrip());
            }
        }
        tripSet.addAll(byMemberName);

        //연도 검색
        Integer year = Integer.parseInt(keyWord);
        List<Trip> byYear = Optional.ofNullable(tripRepository.findByYear(year))
                .orElse(Collections.emptyList());
        tripSet.addAll(byYear);

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
