package graduation.project.DoDutch_server.domain.expense.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryCostDto {
    private String expenseCategory;
    private int cost;
}
