package graduation.project.DoDutch_server.domain.trip.Service;

import graduation.project.DoDutch_server.domain.trip.DTO.Request.TripRequestDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.TripConverter;
import graduation.project.DoDutch_server.domain.trip.Repository.TripRepository;
import graduation.project.DoDutch_server.domain.trip.entity.Trip;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TripServiceImpl implements TripService{
    private final TripRepository tripRepository;

    @Override
    public Long createTrip(TripRequestDTO tripRequestDTO) {
        Trip savedTrip = tripRepository.save(TripConverter.toEntity(tripRequestDTO));
        return savedTrip.getId();
    }
}
