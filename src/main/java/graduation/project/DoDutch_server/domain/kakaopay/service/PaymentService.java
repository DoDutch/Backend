package graduation.project.DoDutch_server.domain.kakaopay.service;


import graduation.project.DoDutch_server.domain.kakaopay.dto.PayApproveResponseDto;
import graduation.project.DoDutch_server.domain.kakaopay.dto.PayReadyRequestDto;
import graduation.project.DoDutch_server.domain.kakaopay.dto.PayReadyResponseDto;
import graduation.project.DoDutch_server.domain.kakaopay.entity.PaymentOrder;
import graduation.project.DoDutch_server.domain.kakaopay.repository.PaymentOrderRepository;
import graduation.project.DoDutch_server.domain.kakaopay.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final KakaopayService kakaopayService;
    private final PaymentOrderRepository orderRepository;
    private final WalletRepository walletRepository;

    @Value("${kakaopay.premium-approval-url}")
    private String approvalUrl;

    private String newPartnerOrderId() {
        return "ORDER_" + UUID.randomUUID();
    }

    @Transactional
    public PayReadyResponseDto ready(PayReadyRequestDto req) {

        String partnerOrderId = newPartnerOrderId();

        PaymentOrder order = PaymentOrder.create(partnerOrderId, req.getRecipientUserId(),
                req.getPayerUserId(), req.getRecipientName() + "에게 송금하기", req.getAmount());

        orderRepository.save(order);

        Map<String, Object> r = kakaopayService.ready(
                partnerOrderId, req.getRecipientUserId(), order.getItemName(), order.getAmount(), approvalUrl);

        order.applyTid((String) r.get("tid"));

        return PayReadyResponseDto.builder()
                .partnerOrderId(partnerOrderId)
                .tid(order.getKakaoTid())
                .redirectUrl((String) r.get("next_redirect_pc_url"))
                .build();
    }

    @Transactional
    public PayApproveResponseDto approve(String partnerOrderId, String pgToken) {

        PaymentOrder order = orderRepository.findByPartnerOrderId(partnerOrderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        String partnerUserId = order.getPartnerUserId();

        Map<String, Object> r = kakaopayService.approve(
                order.getKakaoTid(), partnerOrderId, partnerUserId, pgToken);

        order.approve();  // 결제 성공 처리

        // ✅ 수취인 Wallet 증가
        walletRepository.addBalance(order.getPartnerUserId(), order.getAmount());

        return PayApproveResponseDto.builder()
                .tid(order.getKakaoTid())
                .status("APPROVED")
                .receiverId(partnerUserId)
                .amount(order.getAmount())
                .message("결제 성공!")
                .build();
    }

}
