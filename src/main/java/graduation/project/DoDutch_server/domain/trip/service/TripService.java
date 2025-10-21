package graduation.project.DoDutch_server.domain.trip.service;

import graduation.project.DoDutch_server.domain.trip.dto.Request.PredictRequestDto;
import graduation.project.DoDutch_server.domain.trip.dto.Request.TripJoinRequestDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Request.TripRequestDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripDetailResponseDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripResponseDTO;

import java.io.IOException;
import java.util.List;

public interface TripService {
    Long createTrip(TripRequestDTO tripRequestDTO, Long memberId) throws IOException;
    void joinTrip(TripJoinRequestDTO tripJoinRequestDTO, Long memberId);
    TripResponseDTO shareTrip(Long tripId);
    List<TripDetailResponseDTO> searchTrip(String keyWord);
    TripDetailResponseDTO detailTrip(Long tripId);
    List<Float> predictBudget(PredictRequestDto requestDto);
}
