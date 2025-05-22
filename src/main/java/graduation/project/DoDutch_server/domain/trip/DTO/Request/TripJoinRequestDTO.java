package graduation.project.DoDutch_server.domain.trip.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class TripJoinRequestDTO {
    private String joinCode;
}
