package graduation.project.DoDutch_server.domain.trip.Service;

import graduation.project.DoDutch_server.domain.trip.DTO.Request.TripRequestDTO;

public interface TripService {
    public Long createTrip(TripRequestDTO tripRequestDTO);
}
