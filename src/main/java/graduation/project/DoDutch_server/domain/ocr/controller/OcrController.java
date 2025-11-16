package graduation.project.DoDutch_server.domain.ocr.controller;


import graduation.project.DoDutch_server.domain.ocr.dto.ReceiptItemDto;
import graduation.project.DoDutch_server.domain.ocr.service.OcrService;
import graduation.project.DoDutch_server.global.common.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ocr")
@Tag(name = "Ocr", description = "영수증 ocr 관련 API")
public class OcrController {
    private final OcrService ocrService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "ocr 호출 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<List<ReceiptItemDto>> upload(@RequestPart("file") MultipartFile file) {
        return ApiResponse.onSuccess(
                ocrService.callOcrAndParse(file)
        );
    }
}
