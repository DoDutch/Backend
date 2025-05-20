package graduation.project.DoDutch_server.domain.expense.converter;

import graduation.project.DoDutch_server.domain.expense.dto.*;
import graduation.project.DoDutch_server.domain.expense.entity.Expense;
import graduation.project.DoDutch_server.domain.member.entity.Member;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ExpenseConverter {
    public static List<CategoryCostDto> toCategoryCostDtoList(List<Expense> expenses){
        return expenses.stream()
                .collect(Collectors.groupingBy( //카테로비별 합을 계산
                        Expense::getExpenseCategory,
                        Collectors.summingInt(Expense::getAmount)
                ))
                .entrySet().stream()
                .map(e-> CategoryCostDto.builder() //그 값들을 빌더로 넣어줌
                        .expenseCategory(e.getKey().toString())
                        .cost(e.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    public static List<MemberDto> toMemberDtoList(List<Member> members){
        return members.stream()
                .map(m-> MemberDto.builder()
                        .memberId(m.getId())
                        .nickname(m.getNickname())
                        .build())
                .collect(Collectors.toList());
    }

    public static AllExpenseResponseDto toAllExpenseResponseDto(int budget, int remainingCost, List<Expense> expenses, List<Member> members){ //여행별 전체 지출 조회 response dto
        return AllExpenseResponseDto.builder()
                .budget(budget)
                .remainingCost(remainingCost)
                .categories(toCategoryCostDtoList(expenses))
                .members(toMemberDtoList(members))
                .build();
    }

    public static ExpenseSingleResponseDto toDtoSingleWrapperAllExpense(int budget, //data 붙은 여행별 전체 지출 조회 response dto
                                                         int remainingCost,
                                                         List<Expense> expenses,
                                                         List<Member> members){
        return new ExpenseSingleResponseDto(toAllExpenseResponseDto(budget,remainingCost,expenses,members));
    }

    public static List<AllExpenseByDateResponseDto> toAllExpenseByDateResponseDto(List<Expense> expenses){// 날짜별 전체 지출 조회 response dto
        return expenses.stream()
                .map(expense->AllExpenseByDateResponseDto.builder()
                        .date(expense.getExpenseDate())
                        .cost(expense.getAmount())
                        .title(expense.getTitle())
                        .build())
                .sorted(Comparator.comparing(AllExpenseByDateResponseDto::getDate))
                .collect(Collectors.toList());

    }

    public static ExpenseListReponseDto toDtoListWrapperAllExpenseByDate(List<Expense> expenses){ //data 붙은 날짜별 전체 지출 조회 response dto
        return new ExpenseListReponseDto(toAllExpenseByDateResponseDto(expenses));
    }

    public static ExpenseByExpenseIdResponseDto toExpenseByExpenseIdResponseDto(Expense expense, String tripName){ //지출별 조회
        return ExpenseByExpenseIdResponseDto.builder()
                .tripName(tripName)
                .expenseDate(expense.getExpenseDate())
                .title(expense.getTitle())
                .amount(expense.getAmount())
                .expenseImage(expense.getExpenseImageUrl())
                .memo(expense.getMemo())
                .build();
    }

    public static ExpenseSingleResponseDto toDtoSingleWrapperExpenseByExpenseId(Expense expense, String tripName){ //data 붙은 지출별 조회
        return new ExpenseSingleResponseDto(toExpenseByExpenseIdResponseDto(expense,tripName));
    }


}
