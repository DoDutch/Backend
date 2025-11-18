package graduation.project.DoDutch_server.domain.trip.dto.Request;

import java.time.LocalDate;

public record TripSuggestionRequestDto(
        String place,
        LocalDate startDate,
        LocalDate endDate
) {
}
