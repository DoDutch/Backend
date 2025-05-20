package graduation.project.DoDutch_server.domain.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExpenseSingleResponseDto<T> {
    private T data;
}
