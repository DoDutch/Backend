package graduation.project.DoDutch_server.domain.kakaopay.service;


import graduation.project.DoDutch_server.domain.kakaopay.dto.PayApproveResponseDto;
import graduation.project.DoDutch_server.domain.kakaopay.dto.PayReadyRequestDto;
import graduation.project.DoDutch_server.domain.kakaopay.dto.PayReadyResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 카카오페이 결제 로직 처리 서비스
 */
@Service
@RequiredArgsConstructor
public class KakaopayService {
    private final RestTemplate restTemplate;

    @Value("${kakaopay.base-url}")
    private String baseUrl;

    @Value("${kakaopay.cid}")
    private String cid;

    @Value("${kakaopay.secret-key}")
    private String secretKey;

    @Value("${kakaopay.approval-url}")
    private String approvalUrl;

    @Value("${kakaopay.cancel-url}")
    private String cancelUrl;

    @Value("${kakaopay.fail-url}")
    private String failUrl;

    /**
     * ✅ 결제 준비 요청
     */
    public Map<String, Object> ready(String partnerOrderId, String partnerUserId,
                                     String itemName, Long totalAmount) {
        String url = baseUrl + "/online/v1/payment/ready";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("cid", cid);
        body.put("partner_order_id", partnerOrderId);
        body.put("partner_user_id", partnerUserId);
        body.put("item_name", itemName);
        body.put("quantity", 1);
        body.put("total_amount", totalAmount);
        body.put("vat_amount", 0);
        body.put("tax_free_amount", 0);
        body.put("approval_url", approvalUrl + "?partner_order_id=" + partnerOrderId);
        body.put("cancel_url", cancelUrl + "?partner_order_id=" + partnerOrderId);
        body.put("fail_url", failUrl + "?partner_order_id=" + partnerOrderId);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(url, entity, Map.class);
    }

    /**
     * ✅ 결제 승인 요청
     */
    public Map<String, Object> approve(String tid, String partnerOrderId, String partnerUserId, String pgToken) {
        String url = baseUrl + "/online/v1/payment/approve";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("cid", cid);
        body.put("tid", tid);
        body.put("partner_order_id", partnerOrderId);
        body.put("partner_user_id", partnerUserId);
        body.put("pg_token", pgToken);

        return restTemplate.postForObject(url, new HttpEntity<>(body, headers), Map.class);
    }
}
