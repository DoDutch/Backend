package graduation.project.DoDutch_server.domain.trip.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class TripExpenseDTO {
    private Long expenseId;
    private String photoUrl;
    private LocalDate expenseDate;
    private String title;
    private Integer amount;
    private String memo;
}
