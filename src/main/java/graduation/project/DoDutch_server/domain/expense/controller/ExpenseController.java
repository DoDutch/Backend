package graduation.project.DoDutch_server.domain.expense.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graduation.project.DoDutch_server.domain.expense.dto.*;
import graduation.project.DoDutch_server.domain.expense.service.ExpenseService;
import graduation.project.DoDutch_server.global.common.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/trip")
@Tag(name = "Expense", description = "지출 관련 API")
public class ExpenseController {
    private final ExpenseService expenseService;

    /*
     * 하나의 여행에 대한 전체 지출 목록 조회
     */
    @GetMapping("/{tripId}/expense")
    @Operation(summary = "여행별 전체 지출 조회 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<AllExpenseResponseDto> getExpensesByTrip(@PathVariable("tripId") Long tripId) {
       return ApiResponse.onSuccess(
               expenseService.getExpensesByTripId(tripId)
       );
    }


    /*
     * 하나의 여행에 대한 날짜별 지출 목록 조회
     */
    @GetMapping("/{tripId}/expense/date")
    @Operation(summary = "여행별 날짜별 지출 목록 조회 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<List<AllExpenseByDateResponseDto>> getExpensesByTripAndDate(@PathVariable("tripId") Long tripId){
        return ApiResponse.onSuccess(
                expenseService.getExpensesByTripIdAndDate(tripId)
        );
    }


    /*
     * 지출 세부 조회
     */
    @Operation(summary = "지출 세부 조회 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @GetMapping("/expense/{expenseId}")
    public ApiResponse<ExpenseByExpenseIdResponseDto> getExpensesById(@PathVariable("expenseId") Long expenseId){
        return ApiResponse.onSuccess(
                expenseService.getExpenseByExpenseId(expenseId)
        );
    }


    @Operation(summary = "지출 생성 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PostMapping(value = "/{tripId}/expense", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Object> addExpense(@PathVariable("tripId") Long tripId,
                                          @RequestPart("expenseRequestDto") ExpenseRequestDto expenseRequestDto,
                                          @RequestPart("expenseImages") List<MultipartFile> expenseImages
                                          ) {
        expenseService.addExpense(tripId, expenseRequestDto, expenseImages);

        return ApiResponse.onSuccess();
    }


    @Operation(summary = "지출 수정 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PatchMapping(value = "/{tripId}/expense/{expenseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Object> updateExpense(@PathVariable("tripId") Long tripId,
                                             @PathVariable("expenseId") Long expenseId,
                                             @RequestPart("expenseRequestDto") ExpenseRequestDto expenseRequestDto,
                                             @RequestPart(value = "expenseImages", required = false) List<MultipartFile> expenseImages) {


        expenseService.updateExpense(expenseId, expenseRequestDto, expenseImages);

        return ApiResponse.onSuccess();
    }


}
