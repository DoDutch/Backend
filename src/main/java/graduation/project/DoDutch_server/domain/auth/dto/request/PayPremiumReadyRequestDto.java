package graduation.project.DoDutch_server.domain.auth.dto.request;

public record PayPremiumReadyRequestDto(
        String payerUserId, //보내는 사람
        Long amount
) {
}
