package graduation.project.DoDutch_server.domain.auth.dto.response;

public record PayPremiumReadyResponseDto(
        String partnerOrderId,
        String tid,
        String redirectUrl
) {
}
