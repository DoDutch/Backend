package graduation.project.DoDutch_server.domain.trip.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripRequestDTO {
    private String tripName; // 여행 이름
    private LocalDate startDate; // 시작 날짜
    private LocalDate endDate; // 종료 날짜
    private String place; // 여행지
    private Integer budget; // 예산
//    private MultipartFile tripImage;
    private String tripImage; // aws에 연결하기 전까지 사용하는 임시 변수
}
