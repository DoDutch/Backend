package graduation.project.DoDutch_server.domain.expense.dto;

import graduation.project.DoDutch_server.domain.expense.entity.ExpenseCategory;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseRequestDto {
    private Long payer;
    private String title;
    private ExpenseCategory expenseCategory;
    private int amount;
    private LocalDate expenseDate;
    private String memo;
    private List<MemberShareDto> members;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberShareDto {
        private Long memberId;   // 멤버 ID
        private Integer cost;    // 개인별 금액
    }


}
