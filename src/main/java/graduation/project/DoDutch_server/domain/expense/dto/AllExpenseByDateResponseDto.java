package graduation.project.DoDutch_server.domain.expense.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class AllExpenseByDateResponseDto {
    private LocalDate date;
    private int cost;
    private String title;
}
