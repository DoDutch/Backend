package graduation.project.DoDutch_server.domain.trip.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class TripMemberDTO {
    private Long memberId;
    private String nickName;
}
