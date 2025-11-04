package graduation.project.DoDutch_server.domain.trip.dto.Request;

import java.time.LocalDate;

public record TripUpdateRequestDTO(
        String tripName,
        LocalDate startDate,
        LocalDate endDate,
        String place,
        Integer budget
) {
}
