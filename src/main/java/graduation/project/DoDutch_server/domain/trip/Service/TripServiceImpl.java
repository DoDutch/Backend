package graduation.project.DoDutch_server.domain.trip.Service;

import graduation.project.DoDutch_server.domain.trip.DTO.Request.TripRequestDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.Response.TripDetailResponseDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.Response.TripResponseDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.TripConverter;
import graduation.project.DoDutch_server.domain.trip.Repository.TripRepository;
import graduation.project.DoDutch_server.domain.trip.entity.Trip;
import graduation.project.DoDutch_server.domain.trip.entity.TripMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TripServiceImpl implements TripService{
    private final TripRepository tripRepository;

    //여행 생성
    @Transactional
    @Override
    public Long createTrip(TripRequestDTO tripRequestDTO) {
        Trip savedTrip = tripRepository.save(TripConverter.toEntity(tripRequestDTO));
        return savedTrip.getId();
    }

    //여행 공유시 정보 조회
    @Transactional
    @Override
    public TripResponseDTO shareTrip(Long tripId) {
        Optional<Trip> optionalTrip = tripRepository.findById(tripId);
        return optionalTrip.map(TripConverter::toDto).orElse(null);
    }

    //여행 목록 전체 조회
//    @Override
//    public List<TripResponseDTO> searchTrip(String name, TripMember member, LocalDate date) {
//        List<Optional<Trip>> optionalList = tripRepository.findAllById();
//        return optionalList.stream().map(TripConverter::toDtoList).collect();
//    }

    //여행 정보 조회
    @Override
    @Transactional
    public TripDetailResponseDTO detailTrip(Long tripId) {
        Optional<Trip> optionalTrip = tripRepository.findById(tripId);
        return optionalTrip.map(TripConverter::toDetailDto).orElse(null)    ;
    }
}
