package graduation.project.DoDutch_server.domain.expense.controller;

import graduation.project.DoDutch_server.domain.expense.dto.*;
import graduation.project.DoDutch_server.domain.expense.service.ExpenseService;
import graduation.project.DoDutch_server.global.common.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/trip")
public class ExpenseController {
    private final ExpenseService expenseService;

    /*
    하나의 여행에 대한 전체 지출 조회
     */
    @GetMapping("/{tripId}/expense")
    public ApiResponse<AllExpenseResponseDto> getExpensesByTrip(@PathVariable("tripId") Long tripId) {
       return ApiResponse.onSuccess(
               expenseService.getExpensesByTripId(tripId)
       );
    }

    /*
    하나의 여행에 대한 날짜별 조회
     */
    @GetMapping("/{tripId}/expense/date")
    public ApiResponse<List<AllExpenseByDateResponseDto>> getExpensesByTripAndDate(@PathVariable("tripId") Long tripId){
        return ApiResponse.onSuccess(
                expenseService.getExpensesByTripIdAndDate(tripId)
        );
    }

    /*
    이거 좀 이상함. 원래 목적은 expenseId로 조회하고 거기에 tripId로 이름 가져오기
    지금은 각각 다르게 조회되는 문제 있음...
     */
    @GetMapping("/expense/{expenseId}")
    public ApiResponse<ExpenseByExpenseIdResponseDto> getExpensesById(@PathVariable("expenseId") Long expenseId){
        return ApiResponse.onSuccess(
                expenseService.getExpenseByExpenseId(expenseId)
        );
    }
}
