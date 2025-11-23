package graduation.project.DoDutch_server.domain.trip.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class TripExpenseDTO {
    private Long expenseId; // 지출 id
    private String photoUrl; // 지출 사진 url
    private List<String> expensePhotoUrls; // 지출 사진 url 목록
    private LocalDate expenseDate; // 지출 날짜
    private String title; // 지출 제목
    private Integer amount; // 지출 금액
    private String memo; // 메모
}
