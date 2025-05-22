package graduation.project.DoDutch_server.domain.dutch.controller;

import graduation.project.DoDutch_server.domain.dutch.dto.DutchResponseDTO;
import graduation.project.DoDutch_server.domain.dutch.dto.DutchUpdateRequestDTO;
import graduation.project.DoDutch_server.domain.dutch.service.DutchService;
import graduation.project.DoDutch_server.global.common.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trips/{tripId}/dutchs")
public class DutchController {
    private final DutchService dutchService;

    /*
     * 정산하기
     */
    @PostMapping
    public ApiResponse<List<DutchResponseDTO>> calculateDutch(@PathVariable("tripId") Long tripId) {
        List<DutchResponseDTO> calculatedDutchs = dutchService.calculateDutch(tripId);
        return ApiResponse.onSuccess(calculatedDutchs);
    }


    /*
     * 정산 목록 조회
     */
    @GetMapping
    public ApiResponse<List<DutchResponseDTO>> getAllDutchs(@PathVariable("tripId") Long tripId) {
        List<DutchResponseDTO> dutchs = dutchService.findAllDutchs(tripId);
        return ApiResponse.onSuccess(dutchs);
    }


    /*
     * 정산 세부 내역 조회
     */
    @GetMapping("/{dutchId}")
    public ApiResponse<DutchResponseDTO> getDutchById(@PathVariable("tripId") Long tripId,
                                                      @PathVariable("dutchId") Long dutchId) {
        DutchResponseDTO dutch = dutchService.findDutchById(tripId, dutchId);
        return ApiResponse.onSuccess(dutch);
    }


    /*
     * 정산 여부 완료 표기
     */
    @PatchMapping("/{dutchId}")
    public ApiResponse<Object> updateDutch(@PathVariable("tripId") Long tripId,
                                           @PathVariable("dutchId") Long dutchId,
                                           @RequestBody DutchUpdateRequestDTO dutchUpdateRequestDTO) {

        dutchService.updateDutch(tripId, dutchId, dutchUpdateRequestDTO);
        return ApiResponse.onSuccess();
    }
}
