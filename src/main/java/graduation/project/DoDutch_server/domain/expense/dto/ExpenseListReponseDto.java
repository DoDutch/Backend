package graduation.project.DoDutch_server.domain.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ExpenseListReponseDto<T> {
    private List<T> data;
}
