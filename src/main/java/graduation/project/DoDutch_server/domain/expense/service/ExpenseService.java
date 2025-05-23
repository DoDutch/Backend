package graduation.project.DoDutch_server.domain.expense.service;

import graduation.project.DoDutch_server.domain.expense.converter.ExpenseConverter;
import graduation.project.DoDutch_server.domain.expense.dto.*;
import graduation.project.DoDutch_server.domain.expense.entity.Expense;
import graduation.project.DoDutch_server.domain.expense.repository.ExpenseRepository;
import graduation.project.DoDutch_server.domain.member.entity.Member;
import graduation.project.DoDutch_server.domain.trip.entity.Trip;
import graduation.project.DoDutch_server.domain.trip.entity.TripMember;
import graduation.project.DoDutch_server.domain.trip.repository.TripMemberRepository;
import graduation.project.DoDutch_server.domain.trip.repository.TripRepository;
import graduation.project.DoDutch_server.global.common.apiPayload.code.status.ErrorStatus;
import graduation.project.DoDutch_server.global.common.exception.handler.ErrorHandler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Triple;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseService {

    private final TripRepository tripRepository;
    private final ExpenseRepository expenseRepository;
    private final TripMemberRepository tripMemberRepository;


//    public void addExpense(){
//
//    }

    public AllExpenseResponseDto getExpensesByTripId(Long tripId){ //전체 여행 지출 조회
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.TRIP_NOT_EXIST));

        List<Expense> expenses= expenseRepository.findByTripId(tripId); //지출 기록

        int budget = trip.getBudget(); //전체 예산
        int remainingCost = (trip.getBudget() != null ? trip.getBudget() : 0) -
                (trip.getTotalCost() != null ? trip.getTotalCost() : 0); //잔여금액

        List<TripMember> tripMembers = tripMemberRepository.findByTripId(tripId);

        List<Member> members = tripMembers.stream() //Member 형태로 변환
                .map(TripMember::getMember)
                .collect(Collectors.toList());

        return ExpenseConverter.toAllExpenseResponseDto(budget,remainingCost,expenses, members);

    }

    public List<AllExpenseByDateResponseDto> getExpensesByTripIdAndDate(Long tripId){ //날짜별 여행 지출 조회
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.TRIP_NOT_EXIST));

        List<Expense> expenses= expenseRepository.findByTripId(tripId);

        return ExpenseConverter.toAllExpenseByDateResponseDto(expenses);
    }

    public ExpenseByExpenseIdResponseDto getExpenseByExpenseId(Long expenseId){

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.EXPENSE_NOT_EXIST));

//        Trip trip = tripRepository.findById(tripId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 여행이 존재하지 않습니다: " + tripId));

        String tripName = expense.getTrip().getName();

        return ExpenseConverter.toExpenseByExpenseIdResponseDto(expense, tripName);

    }

}
