package graduation.project.DoDutch_server.domain.trip.Controller;

import graduation.project.DoDutch_server.domain.trip.DTO.Request.TripJoinRequestDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.Request.TripRequestDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.Response.TripDetailResponseDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.Response.TripResponseDTO;
import graduation.project.DoDutch_server.domain.trip.Service.TripServiceImpl;
import graduation.project.DoDutch_server.domain.trip.entity.Trip;
import graduation.project.DoDutch_server.domain.trip.entity.TripMember;
import graduation.project.DoDutch_server.global.common.ResponseDTO;
import graduation.project.DoDutch_server.global.common.apiPayload.ApiResponse;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/trip")
@RequiredArgsConstructor
public class TripController {
    private final TripServiceImpl tripService;

    /*
    여행생성
     */
//    @PostMapping
//    public ResponseEntity<ResponseDTO<Long>> tripRegister(@RequestBody TripRequestDTO tripRequestDTO) {
//        ResponseDTO<Long> responseDTO;
//
//
//    }

    /*
    여행 참여
     */
    @PostMapping("/join")
    public ApiResponse<Void> tripJoin(@RequestBody TripJoinRequestDTO tripJoinRequestDTO) {
        //Todo: 시큐리티로 참여자 id 넘겨주기.
        Long memberId = 1L;

        tripService.joinTrip(tripJoinRequestDTO, memberId);
        return ApiResponse.onSuccess();
    }

    /*
    여행 공유시 정보 조회하기
     */
    @GetMapping("/share/{tripId}")
    public ApiResponse<TripResponseDTO> shareTripInfo(@PathVariable("tripId") Long tripId){
        TripResponseDTO tripResponseDTO = tripService.shareTrip(tripId);
        return ApiResponse.onSuccess(tripResponseDTO);
    }

    /*
    여행 목록 전체 조회(시간순)
     */
    @GetMapping("/search")
    public ApiResponse<List<TripResponseDTO>> searchTrip(
            @RequestParam(value = "keyWord")String keyWord){
        List<TripResponseDTO> responseDTOList = tripService.searchTrip(keyWord);
        return ApiResponse.onSuccess(responseDTOList);
    }

    /*
    여행별 조회
     */
    @GetMapping("{tripId}")
    public ApiResponse<TripDetailResponseDTO> detailTripInfo(@PathVariable("tripId") Long tripId){
        TripDetailResponseDTO tripDetailResponseDTO = tripService.detailTrip(tripId);
        return ApiResponse.onSuccess(tripDetailResponseDTO);
    }
}
