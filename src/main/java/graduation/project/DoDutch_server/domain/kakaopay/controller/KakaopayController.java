package graduation.project.DoDutch_server.domain.kakaopay.controller;


import graduation.project.DoDutch_server.domain.kakaopay.dto.PayApproveResponseDto;
import graduation.project.DoDutch_server.domain.kakaopay.dto.PayReadyRequestDto;
import graduation.project.DoDutch_server.domain.kakaopay.dto.PayReadyResponseDto;
import graduation.project.DoDutch_server.domain.kakaopay.service.PaymentService;
import graduation.project.DoDutch_server.global.common.apiPayload.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/kakaopay")
@RequiredArgsConstructor
public class KakaopayController {

    private final PaymentService paymentService;

    @PostMapping("/ready")
    public ApiResponse<PayReadyResponseDto> ready(@RequestBody PayReadyRequestDto req) {
        PayReadyResponseDto response = paymentService.ready(req);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/approve-callback")
    public void approve(
            @RequestParam("partner_order_id") String partnerOrderId,
            @RequestParam("pg_token") String pgToken,
            HttpServletResponse response
    ) throws IOException {

        PayApproveResponseDto result = paymentService.approve(partnerOrderId, pgToken);

        String redirectUrl =
                "http://localhost:3000/payment/success"
                        + "?tid=" + result.getTid()
                        + "&receiverId=" + result.getReceiverId()
                        + "&amount=" + result.getAmount();

        response.sendRedirect(redirectUrl);
    }

//
//    @GetMapping("/cancel-callback")
//    public String cancel(@RequestParam("partner_order_id") String id) {
//        return "사용자가 결제를 취소했습니다. partnerOrderId=" + id;
//    }
//
//    @GetMapping("/fail-callback")
//    public String fail(@RequestParam("partner_order_id") String id) {
//        return "결제 실패! partnerOrderId=" + id;
//    }
}
