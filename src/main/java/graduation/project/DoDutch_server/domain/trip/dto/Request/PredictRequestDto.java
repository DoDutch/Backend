package graduation.project.DoDutch_server.domain.trip.dto.Request;

import graduation.project.DoDutch_server.domain.trip.entity.Place;

import java.time.LocalDate;

public record PredictRequestDto(
        Place place,
        LocalDate startDate,
        LocalDate endDate,
        Long numCompanions
) {
}
