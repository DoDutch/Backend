package graduation.project.DoDutch_server.domain.trip.DTO.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TripMemberDTO {
    private Long memberId; // 멤버 id
    private String nickName; // 멤버 별명
}
