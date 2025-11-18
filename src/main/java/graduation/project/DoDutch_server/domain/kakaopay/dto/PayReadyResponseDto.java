package graduation.project.DoDutch_server.domain.kakaopay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 카카오페이 결제 준비 API 응답 DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayReadyResponseDto {
    private String partnerOrderId;
    private String tid;
    private String redirectUrl;

}
