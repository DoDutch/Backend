package graduation.project.DoDutch_server.domain.trip.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripRequestDTO {
    private String name; // 여행 이름
    private LocalDate startDate; // 시작 날짜
    private LocalDate endDate; // 종료 날짜
    private String place; // 여행지
    private Integer budget; // 예산
//    private MultipartFile tripImage;
}
