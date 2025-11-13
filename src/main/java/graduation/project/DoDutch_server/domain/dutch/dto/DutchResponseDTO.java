package graduation.project.DoDutch_server.domain.dutch.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DutchResponseDTO {
    private Long dutchId;
    private MemberInfo payer;
    private MemberInfo payee;
    private Integer perCost;
    private Boolean isCompleted;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberInfo {
        private Long memberId;
        private String nickname;
    }
}
