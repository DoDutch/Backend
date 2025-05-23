package graduation.project.DoDutch_server.domain.trip.controller;

import io.swagger.v3.oas.annotations.Operation;
import graduation.project.DoDutch_server.domain.trip.dto.Request.TripJoinRequestDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Request.TripRequestDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripDetailResponseDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripResponseDTO;
import graduation.project.DoDutch_server.domain.trip.service.TripServiceImpl;
import graduation.project.DoDutch_server.global.common.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trip")
@RequiredArgsConstructor
@Tag(name = "Trip", description = "여행 관련 API")
public class TripController {
    private final TripServiceImpl tripService;

    /*
     * 여행 생성
     */
    @PostMapping
    @Operation(summary = "여행 생성 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<Long> tripRegister(@RequestBody TripRequestDTO tripRequestDTO) { //아마존 연결 후 RequestBody를 @ModelAttribute 수정
        //Todo: 여행 생성자의 member id 넘겨주기.
        Long memberId = 1L;

        Long tripId = tripService.createTrip(tripRequestDTO, memberId);
        return ApiResponse.onSuccess(tripId);
    }

    /*
     * 여행 참여
     */
    @PostMapping("/join")
    @Operation(summary = "여행 참여 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<Void> tripJoin(@RequestBody TripJoinRequestDTO tripJoinRequestDTO) {
        //Todo: 여행 참여자의 id 넘겨주기.
        Long memberId = 1L;

        tripService.joinTrip(tripJoinRequestDTO, memberId);
        return ApiResponse.onSuccess();
    }

    /*
     * 여행 공유 시 정보 조회
     */
    @GetMapping("/share/{tripId}")
    @Operation(summary = "여행 공유 시 정보 반환 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<TripResponseDTO> shareTripInfo(@PathVariable("tripId") Long tripId){
        TripResponseDTO tripResponseDTO = tripService.shareTrip(tripId);
        return ApiResponse.onSuccess(tripResponseDTO);
    }

    /*
     * 여행 목록 전체 조회(시간순)
     */
    @GetMapping("/search")
    @Operation(summary = "여행 목록 전체 조회(시간순) API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<List<TripDetailResponseDTO>> searchTrip(@RequestParam(value = "keyWord")String keyWord){
        List<TripDetailResponseDTO> responseDTOList = tripService.searchTrip(keyWord);
        return ApiResponse.onSuccess(responseDTOList);
    }

    /*
     * 여행별 조회
     */
    @GetMapping("{tripId}")
    @Operation(summary = "여행별 조회 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<TripDetailResponseDTO> detailTripInfo(@PathVariable("tripId") Long tripId){
        TripDetailResponseDTO tripDetailResponseDTO = tripService.detailTrip(tripId);
        return ApiResponse.onSuccess(tripDetailResponseDTO);
    }
}
