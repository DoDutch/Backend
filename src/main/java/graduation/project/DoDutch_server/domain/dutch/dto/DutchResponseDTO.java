package graduation.project.DoDutch_server.domain.dutch.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DutchResponseDTO {
    private Long payer;
    private Long payee;
    private Integer perCost;
    private Boolean isCompleted;
}
