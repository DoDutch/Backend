package graduation.project.DoDutch_server.domain.ocr.service;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import graduation.project.DoDutch_server.domain.ocr.dto.OcrResponse;
import graduation.project.DoDutch_server.domain.ocr.dto.ReceiptItemDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.*;


@Service
@RequiredArgsConstructor
public class OcrService {

    @Value("${naver.service.base-url}")
    private String baseUrl;

    @Value("${naver.service.endpoint}")
    private String endpoint;

    @Value("${naver.service.secretKey}")
    private String secretKey;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * 영수증 OCR 요청 (MultipartFile → Base64 → Clova 호출)
     */
    public OcrResponse requestReceiptOcr(MultipartFile file) throws IOException {

        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

        Map<String, Object> imageData = new HashMap<>();
        imageData.put("format", getFormat(file));   // jpg, png
        imageData.put("name", "receipt");
        imageData.put("data", base64Image);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("version", "V2");
        requestBody.put("requestId", UUID.randomUUID().toString());
        requestBody.put("timestamp", System.currentTimeMillis());
        requestBody.put("images", new Map[]{imageData});

        return webClient.post()
                .uri(endpoint)
                .header("Content-Type", "application/json")
                .header("X-OCR-SECRET", secretKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(OcrResponse.class)
                .block();
    }

    /**
     * 영수증 OCR 결과 → ReceiptItemDto 리스트로 변환
     */
    public List<ReceiptItemDto> parseItems(OcrResponse response) {

        List<ReceiptItemDto> result = new ArrayList<>();

        var imageResult = response.getImages().get(0);
        if (imageResult.getReceipt() == null) return result;

        var receipt = imageResult.getReceipt();
        if (receipt.getResult() == null) return result;

        var receiptResult = receipt.getResult();
        if (receiptResult.getSubResults() == null) return result;

        for (var sub : receiptResult.getSubResults()) {

            if (sub.getItems() == null) continue;

            for (var item : sub.getItems()) {

                // null-safe 처리 추가
                var priceObj = item.getPrice();

                String name = safeText(item.getName());
                int qty = safeInt(item.getCount());
                int unitPrice = (priceObj != null)
                        ? safePrice(priceObj.getUnitPrice())
                        : 0;
                int totalPrice = (priceObj != null)
                        ? safePrice(priceObj.getPrice())
                        : 0;

                // 가격이 없는 라인은 스킵해도 됨
                if (unitPrice == 0 && totalPrice == 0) continue;

                result.add(new ReceiptItemDto(name, unitPrice, qty, totalPrice));
            }
        }

        return result;
    }


    /* ===== Utility ===== */

    private String getFormat(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) return "jpg";
        return contentType.replace("image/", ""); // image/jpeg → jpeg
    }

    private String safeText(OcrResponse.ImageResult.CommonField field) {
        return field != null ? field.getText() : "";
    }

    private int safeInt(OcrResponse.ImageResult.CommonField field) {
        try {
            if (field == null || field.getText() == null) return 1;
            return Integer.parseInt(field.getText().replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 1;
        }
    }

    private int safePrice(OcrResponse.ImageResult.CommonField field) {
        try {
            if (field == null || field.getText() == null) return 0;
            return Integer.parseInt(field.getText().replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }
}