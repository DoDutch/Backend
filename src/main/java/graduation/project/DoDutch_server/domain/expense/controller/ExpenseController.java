package graduation.project.DoDutch_server.domain.expense.controller;

import graduation.project.DoDutch_server.domain.expense.dto.ExpenseListReponseDto;
import graduation.project.DoDutch_server.domain.expense.dto.ExpenseSingleResponseDto;
import graduation.project.DoDutch_server.domain.expense.service.ExpenseService;
import graduation.project.DoDutch_server.global.common.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/trip")
public class ExpenseController {
    private final ExpenseService expenseService;

    @GetMapping("/{tripId}/expense")
    public ApiResponse<ExpenseSingleResponseDto> getExpensesByTrip(@PathVariable("tripId") Long tripId) {
       return ApiResponse.onSuccess(
               expenseService.getExpensesByTripId(tripId)
       );
    }

    @GetMapping("/{tripId}/expense/date")
    public ApiResponse<ExpenseListReponseDto> getExpensesByTripAndDate(@PathVariable("tripId") Long tripId){
        return ApiResponse.onSuccess(
                expenseService.getExpensesByTripIdAndDate(tripId)
        );
    }
}
