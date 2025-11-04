package graduation.project.DoDutch_server.domain.kakaopay.dto;

import lombok.Data;

@Data
public class PayReadyRequestDto {
    private String payerUserId; //보내는 사람
    private String recipientUserId; //받는 사람
    private String recipientName; //받는 사람 이름
    private Long amount; //보내는 금액
}
