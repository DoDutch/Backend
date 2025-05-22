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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

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
        for (Iterator<TripMember> tripMemberIterator = tripMemberList.iterator(); tripMemberIterator.hasNext();){
            TripMember tripMember = tripMemberIterator.next();
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
        return optionalTrip.map(TripConverter::toDto).orElse(null);
    }

    /*
    여행 목록 전체 조회
     */
    @Override
    public List<TripResponseDTO> searchTrip(String keyWord) {
        //Todo: keyWord를 각각 여행 이름 검색, 멤버 이름(여행들 중에서 여행 참여자의 이름과 같은 여행들을 갖고온다.) 검색,
        // 연도 검색 모두 이용하고 각각의 검색 결과를 모두 반환.
        List<Trip> tripList = tripRepository.findByName(keyWord);
        List<TripMember> tripMemberList = tripMemberRepository.findAll();

        return null;
    }

    /*
    여행 정보 조회
     */
    @Override
    @Transactional
    public TripDetailResponseDTO detailTrip(Long tripId) {
        Optional<Trip> optionalTrip = tripRepository.findById(tripId);
        if (optionalTrip.isEmpty()) throw new ErrorHandler(ErrorStatus.TRIP_NOT_EXIST);
        return optionalTrip.map(TripConverter::toDetailDto).orElse(null);
    }
}
