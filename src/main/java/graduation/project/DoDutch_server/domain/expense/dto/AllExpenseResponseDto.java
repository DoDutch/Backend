package graduation.project.DoDutch_server.domain.expense.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class AllExpenseResponseDto {
    private int budget;
    private int remainingCost;
    private List<CategoryCostDto> categories;
    private List<MemberDto> members;
}
