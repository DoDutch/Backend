package graduation.project.DoDutch_server.domain.trip.dto.Request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class TripUpdateRequestDTO {
    private String tripName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String place;
    private Integer budget;
    private MultipartFile tripImage;
}
