package graduation.project.DoDutch_server.domain.trip.DTO;

import graduation.project.DoDutch_server.domain.trip.DTO.Request.TripRequestDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.Response.TripResponseDTO;
import graduation.project.DoDutch_server.domain.trip.entity.Trip;

import java.util.List;
import java.util.stream.Collectors;

public class TripConverter {
    public static Trip toEntity(TripRequestDTO tripRequestDTO) {
        return Trip.builder()
                .name(tripRequestDTO.getName())
                .place(tripRequestDTO.getPlace())
                .startDate(tripRequestDTO.getStartDate())
                .endDate(tripRequestDTO.getEndDate())
                .budget(tripRequestDTO.getBudget())
//                .tripImageUrl() //Todo: s3Manager을 통한 이미지 처리
                .build();
    }
}
