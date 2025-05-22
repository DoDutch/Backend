package graduation.project.DoDutch_server.domain.trip.Service;

import graduation.project.DoDutch_server.domain.trip.DTO.Request.TripJoinRequestDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.Request.TripRequestDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.Response.TripDetailResponseDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.Response.TripResponseDTO;
import graduation.project.DoDutch_server.domain.trip.entity.TripMember;

import java.time.LocalDate;
import java.util.List;

public interface TripService {
    public Long createTrip(TripRequestDTO tripRequestDTO);
    public Void joinTrip(TripJoinRequestDTO tripJoinRequestDTO, Long memberId);
    public TripResponseDTO shareTrip(Long tripId);
    public List<TripDetailResponseDTO> searchTrip(String keyWord);
    public TripDetailResponseDTO detailTrip(Long tripId);
}
