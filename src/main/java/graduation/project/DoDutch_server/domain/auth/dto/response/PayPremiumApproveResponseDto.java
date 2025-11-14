package graduation.project.DoDutch_server.domain.auth.dto.response;

public record PayPremiumApproveResponseDto(
        String tid,
        String status,
        String buyerId,
        Long amount,
        String message
) {
}
