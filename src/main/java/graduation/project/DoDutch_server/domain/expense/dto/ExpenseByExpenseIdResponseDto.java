package graduation.project.DoDutch_server.domain.expense.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ExpenseByExpenseIdResponseDto {
    private String tripName;
    private LocalDate expenseDate;
    private String title;
    private int amount;
    private String expenseImage;
    private String memo;
}
