package graduation.project.DoDutch_server.domain.ocr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)  // ★ 추가
public class OcrResponse {

    private String version;
    private String requestId;
    private long timestamp;
    private List<ImageResult> images;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true) // ★ 추가
    public static class ImageResult {
        private String inferResult;
        private String message;
        private List<Field> fields;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true) // ★ 추가
    public static class Field {
        private String inferText;
        private BoundingPoly boundingPoly;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true) // ★ 추가
    public static class BoundingPoly {
        private List<Vertex> vertices;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true) // ★ 추가
    public static class Vertex {
        private double x;
        private double y;
    }
}