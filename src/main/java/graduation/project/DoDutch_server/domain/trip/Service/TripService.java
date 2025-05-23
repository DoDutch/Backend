package graduation.project.DoDutch_server.domain.trip.Service;

import graduation.project.DoDutch_server.domain.trip.DTO.Request.TripJoinRequestDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.Request.TripRequestDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.Response.TripDetailResponseDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.Response.TripResponseDTO;

import java.util.List;

public interface TripService {
    Long createTrip(TripRequestDTO tripRequestDTO, Long memberId);
    void joinTrip(TripJoinRequestDTO tripJoinRequestDTO, Long memberId);
    TripResponseDTO shareTrip(Long tripId);
    List<TripDetailResponseDTO> searchTrip(String keyWord);
    TripDetailResponseDTO detailTrip(Long tripId);
}
