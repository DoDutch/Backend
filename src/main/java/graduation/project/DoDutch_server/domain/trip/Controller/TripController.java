package graduation.project.DoDutch_server.domain.trip.Controller;

import graduation.project.DoDutch_server.domain.trip.DTO.Request.TripRequestDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.Response.TripDetailResponseDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.Response.TripResponseDTO;
import graduation.project.DoDutch_server.domain.trip.Service.TripServiceImpl;
import graduation.project.DoDutch_server.domain.trip.entity.Trip;
import graduation.project.DoDutch_server.domain.trip.entity.TripMember;
import graduation.project.DoDutch_server.global.common.ResponseDTO;
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

    //여행생성
//    @PostMapping
//    public ResponseEntity<ResponseDTO<Long>> tripRegister(@RequestBody TripRequestDTO tripRequestDTO) {
//        ResponseDTO<Long> responseDTO;
//
//
//    }

    //여행 공유시 정보 조회하기
    @GetMapping("/share/{tripId}")
    public ResponseEntity<ResponseDTO<TripResponseDTO>> shareTripInfo(@PathVariable("tripId") Long tripId){
        TripResponseDTO tripResponseDTO = tripService.shareTrip(tripId);
        ResponseDTO<TripResponseDTO> responseDTO = new ResponseDTO<>();
        if (tripResponseDTO != null){
            responseDTO.setData(tripResponseDTO);
            responseDTO.setCode("COMMON200");
            responseDTO.setMessage("성공입니다");
            responseDTO.setSuccess(true);
            return ResponseEntity.ok(responseDTO);
        }
        else {
            responseDTO.setSuccess(false);
            responseDTO.setCode("400");
            responseDTO.setMessage("존재하지 않는 여행입니다.");
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
    //여행 목록 전체 조회(시간순)
//    @GetMapping("/search")
//    public ResponseEntity<ResponseDTO<List<TripResponseDTO>>> searchTrip(
//            @RequestParam(value = "name")String name, @RequestParam(value = "date")Integer date,
//            @RequestParam(value = "member")TripMember member){
//
//        tripService.searchTrip(name, member, date);
//
//    }
    //여행별 조회
    @GetMapping("{tripId}")
    public ResponseEntity<ResponseDTO<TripDetailResponseDTO>> detailTripInfo(@PathVariable("tripId") Long tripId){
        TripDetailResponseDTO tripDetailResponseDTO = tripService.detailTrip(tripId);
        ResponseDTO<TripDetailResponseDTO> responseDTO = new ResponseDTO<>();
        if (tripDetailResponseDTO != null){
            responseDTO.setData(tripDetailResponseDTO);
            responseDTO.setCode("COMMON200");
            responseDTO.setMessage("성공입니다");
            responseDTO.setSuccess(true);
            return ResponseEntity.ok(responseDTO);
        }
        else {
            responseDTO.setSuccess(false);
            responseDTO.setCode("404");
            responseDTO.setMessage("해당 여행이 존재하지 않습니다");
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
    }
}
