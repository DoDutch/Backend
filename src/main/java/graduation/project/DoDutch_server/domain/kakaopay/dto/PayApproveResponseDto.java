package graduation.project.DoDutch_server.domain.kakaopay.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 카카오페이 결제 승인 API 응답 DTO
 */
@Data
@AllArgsConstructor
@Builder
public class PayApproveResponseDto {
    private String tid;
    private String status;   // APPROVED
    private String receiverId;   // 충전된 사용자
    private Long amount;     // 충전 금액
    private String message;
}
