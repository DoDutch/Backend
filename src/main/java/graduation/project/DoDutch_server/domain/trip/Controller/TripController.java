package graduation.project.DoDutch_server.domain.trip.Controller;

import graduation.project.DoDutch_server.domain.trip.DTO.Request.TripRequestDTO;
import graduation.project.DoDutch_server.domain.trip.Service.TripServiceImpl;
import graduation.project.DoDutch_server.global.common.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trip")
@RequiredArgsConstructor
public class TripController {
    private final TripServiceImpl tripService;

    //여행생성
    @PostMapping
    private ResponseEntity<ResponseDTO<Long>> tripRegister(@RequestBody TripRequestDTO tripRequestDTO) {
        ResponseDTO<Long> responseDTO;


    }

}
