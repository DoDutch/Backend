package graduation.project.DoDutch_server.domain.trip.dto.Request;

import java.time.LocalDateTime;

public record TripSuggestionRequestDto(
        String place,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
