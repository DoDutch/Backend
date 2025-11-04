package graduation.project.DoDutch_server.domain.trip.controller;

import graduation.project.DoDutch_server.domain.trip.dto.Request.PredictRequestDto;
import graduation.project.DoDutch_server.domain.trip.dto.Response.PredictResponseDto;
import graduation.project.DoDutch_server.global.common.apiPayload.code.status.ErrorStatus;
import graduation.project.DoDutch_server.global.common.exception.handler.ErrorHandler;
import graduation.project.DoDutch_server.domain.trip.dto.Request.TripSuggestionRequestDto;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripSuggestionResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import graduation.project.DoDutch_server.domain.trip.dto.Request.TripJoinRequestDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Request.TripRequestDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripDetailResponseDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripResponseDTO;
import graduation.project.DoDutch_server.domain.trip.service.TripServiceImpl;
import graduation.project.DoDutch_server.global.common.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trip")
@RequiredArgsConstructor
@Tag(name = "Trip", description = "여행 관련 API")
public class TripController {
    private final TripServiceImpl tripService;
    private final RestTemplate restTemplate =  new RestTemplate();

    /*
     * 여행 생성
     */
    @PostMapping
    @Operation(summary = "여행 생성 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<Long> tripRegister(@ModelAttribute TripRequestDTO tripRequestDTO) throws IOException { //아마존 연결 후 RequestBody를 @ModelAttribute 수정
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
        Long memberId = 2L;

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
     * 여행 검색
     */
    @GetMapping("/search")
    @Operation(summary = "여행 검색 API")
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
    @GetMapping("/{tripId}")
    @Operation(summary = "여행별 조회 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<TripDetailResponseDTO> detailTripInfo(@PathVariable("tripId") Long tripId){
        TripDetailResponseDTO tripDetailResponseDTO = tripService.detailTrip(tripId);
        return ApiResponse.onSuccess(tripDetailResponseDTO);
    }

    /*
     * 여행 경비 예측
     */
    @PostMapping("/predict")
    @Operation(summary = "여행 경비 예측 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<PredictResponseDto> predictTrip(@RequestBody PredictRequestDto requestDto){
        try {
            // feature 생성
            List<Float> features = tripService.predictBudget(requestDto);

            // Flask 서버로 전달
            String url = "http://localhost:5000/predict";
            Map<String, Object> body = Map.of("features", features);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            Double predicted = (Double) response.getBody().get("predicted_total_cost");


            PredictResponseDto responseDto = new PredictResponseDto(predicted.intValue()+" 원");

            return ApiResponse.onSuccess(responseDto);

        } catch (Exception e) {
            throw new ErrorHandler(ErrorStatus._INTERNAL_SERVER_ERROR);
        }

    }

    /*
     * gpt 여행지 추천
     */
    @PostMapping("/chat")
    @Operation(summary = "여행지 추천 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<TripSuggestionResponseDto> tripRecommend(
            @RequestBody TripSuggestionRequestDto requestDto
            ){
        TripSuggestionResponseDto responseDto = tripService.recommendTrip(requestDto);
        return ApiResponse.onSuccess(responseDto);
    }
}
