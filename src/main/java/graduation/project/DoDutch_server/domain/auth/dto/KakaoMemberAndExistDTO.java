package graduation.project.DoDutch_server.domain.auth.dto;

import graduation.project.DoDutch_server.domain.member.entity.Member;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoMemberAndExistDTO {
    private Member member;
    private boolean isExistingMember;
}
