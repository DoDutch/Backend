package graduation.project.DoDutch_server.domain.ocr.service;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import graduation.project.DoDutch_server.domain.ocr.dto.OcrResponse;
import graduation.project.DoDutch_server.domain.ocr.dto.ReceiptItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OcrService {
    @Value("${clova.ocr.invoke-url}")
    private String invokeUrl;

    @Value("${clova.ocr.secret-key}")
    private String secretKey;

    private final RestTemplate clovaRestTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // ★ 추가;

    public String callOcr(MultipartFile file) {

        try {
            // 1) Base64 변환
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

            // 2) JSON Body 구성
            Map<String, Object> image = new HashMap<>();
            image.put("format", "jpg");
            image.put("name", "receipt");
            image.put("data", base64Image);

            Map<String, Object> body = new HashMap<>();
            body.put("version", "V1");
            body.put("requestId", UUID.randomUUID().toString());
            body.put("timestamp", System.currentTimeMillis());
            body.put("images", List.of(image));

            // 3) Header 구성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-OCR-SECRET", secretKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            // 4) POST 요청
            String result = clovaRestTemplate.postForObject(invokeUrl, request, String.class);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("OCR 호출 실패", e);
        }
    }


    public List<ReceiptItemDto> parseItems(OcrResponse response) {

        List<ReceiptItemDto> result = new ArrayList<>();
        List<OcrResponse.Field> all = response.getImages().get(0).getFields();

        double MIN_Y = 1650;
        double MAX_Y = 2700;

        Pattern numberPattern = Pattern.compile("-?\\d{1,3}(,\\d{3})*");

        // 1) 상품영역 필터링 + y정렬
        List<OcrResponse.Field> fields = all.stream()
                .filter(f -> {
                    double y = f.getBoundingPoly().getVertices().get(0).getY();
                    return y >= MIN_Y && y <= MAX_Y;
                })
                .sorted(Comparator.comparingDouble(
                        f -> f.getBoundingPoly().getVertices().get(0).getY()
                ))
                .toList();

        // 2) Row 분리 (평균 y 기반)
        List<List<OcrResponse.Field>> rows = new ArrayList<>();
        List<OcrResponse.Field> current = new ArrayList<>();
        double lastAvgY = -1;

        for (OcrResponse.Field f : fields) {
            double y = f.getBoundingPoly().getVertices().get(0).getY();

            if (lastAvgY != -1 && Math.abs(y - lastAvgY) > 40) {
                rows.add(current);
                current = new ArrayList<>();
            }

            current.add(f);

            lastAvgY = current.stream()
                    .mapToDouble(ff -> ff.getBoundingPoly().getVertices().get(0).getY())
                    .average().orElse(y);
        }
        if (!current.isEmpty()) rows.add(current);

        // 3) Row별 파싱
        for (List<OcrResponse.Field> row : rows) {

            // 할인 / M+N 라인 제거
            if (row.stream().anyMatch(f ->
                    f.getInferText().contains("-") ||
                            f.getInferText().contains("M+N"))) {
                continue;
            }

            // x 기준 정렬
            row.sort(Comparator.comparingDouble(
                    f -> f.getBoundingPoly().getVertices().get(0).getX()
            ));

            List<String> names = new ArrayList<>();
            List<Integer> numbers = new ArrayList<>();

            // Row 내부 분석
            for (OcrResponse.Field f : row) {
                String text = f.getInferText().replace(" ", "");

                if (numberPattern.matcher(text).matches()) {
                    int n = Integer.parseInt(text.replace(",", ""));
                    numbers.add(n);
                } else {
                    names.add(text);
                }
            }

            // 숫자가 3개 이상 있어야 정상 상품 라인
            if (numbers.size() < 3) continue;

            // 숫자 규칙: [단가, 총액] 양 끝, 가운데 수량
            int unitPrice = numbers.get(0);
            int totalPrice = numbers.get(numbers.size() - 1);

            // 수량은 1자리이므로 가운데로 찾음
            int quantity = -1;
            for (int n : numbers) {
                if (n <= 9) {
                    quantity = n;
                    break;
                }
            }
            if (quantity == -1) continue;

            String name = String.join("", names);

            result.add(new ReceiptItemDto(name, unitPrice, quantity, totalPrice));
        }

        return result;
    }

    public List<ReceiptItemDto> callOcrAndParse(MultipartFile file) {

        try {
            // 1) OCR 호출 -> JSON
            String json = callOcr(file);

            // 2) JSON → OcrResponse DTO로 변환
            OcrResponse response = objectMapper.readValue(json, OcrResponse.class);

            // 3) 파싱 로직 실행
            return parseItems(response);

        } catch (Exception e) {
            throw new RuntimeException("OCR 파싱 실패", e);
        }
    }
}
