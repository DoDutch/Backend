package graduation.project.DoDutch_server.domain.expense.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDto {
    private Long memberId;
    private String nickname;
}
