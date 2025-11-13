package graduation.project.DoDutch_server.domain.expense.service;

import graduation.project.DoDutch_server.domain.expense.converter.ExpenseConverter;
import graduation.project.DoDutch_server.domain.expense.dto.*;
import graduation.project.DoDutch_server.domain.expense.entity.Expense;
import graduation.project.DoDutch_server.domain.expense.entity.ExpenseMember;
import graduation.project.DoDutch_server.domain.expense.repository.ExpenseMemberRepository;
import graduation.project.DoDutch_server.domain.expense.repository.ExpenseRepository;
import graduation.project.DoDutch_server.domain.member.entity.Member;
import graduation.project.DoDutch_server.domain.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;
    private final ExpenseMemberRepository expenseMemberRepository;


    public void addExpense(Long tripId, ExpenseRequestDto expenseRequestDto){

        // tripId로 TripMember 테이블에서 멤버 조회
        List<TripMember> tripMembers = tripMemberRepository.findByTripId(tripId);

        // TripMember의 memberId 리스트 추출
        List<Long> requiredMemberIds = tripMembers.stream()
                .map(tripMember -> tripMember.getMember().getId())
                .toList();

        // Request의 members 리스트에서 memberId 추출
        List<Long> providedMemberIds = expenseRequestDto.getMembers().stream()
                .map(ExpenseRequestDto.MemberShareDto::getMemberId)
                .toList();

        // 모든 멤버가 제공되었는지 검증
        if (!requiredMemberIds.containsAll(providedMemberIds) || !providedMemberIds.containsAll(requiredMemberIds)) {
            throw new IllegalArgumentException("모든 사람의 값을 입력해라");
        }

        // amount 값과 members의 cost 합 검증
        int totalCost = expenseRequestDto.getMembers().stream()
                .mapToInt(ExpenseRequestDto.MemberShareDto::getCost)
                .sum();

        if (!(expenseRequestDto.getAmount()==totalCost)) {
            throw new IllegalArgumentException("총합이 일치하지 않습니다");
        }

        // 결제자(Member) 조회
        Member payer = memberRepository.findById(expenseRequestDto.getPayer())
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // Trip 조회
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.TRIP_NOT_EXIST));

        // Expense 저장
        Expense expense = Expense.builder()
                .title(expenseRequestDto.getTitle())
                .expenseCategory(expenseRequestDto.getExpenseCategory())
                .amount(expenseRequestDto.getAmount())
                .expenseDate(expenseRequestDto.getExpenseDate())
                .memo(expenseRequestDto.getMemo())
                .payer(payer)
                .trip(trip)
                .build();



        expenseRepository.save(expense);

        // Trip의 totalCost 업데이트
        updateTotalCost(trip, expense.getAmount());

        // ExpenseMember 생성 및 저장
        for (ExpenseRequestDto.MemberShareDto memberDto : expenseRequestDto.getMembers()) {

            //tripMember 객체 찾기
            TripMember tripMember = tripMemberRepository.findByTripIdAndMemberId(tripId, memberDto.getMemberId())
                    .orElseThrow(() -> new ErrorHandler(ErrorStatus.TRIP_MEMBER_EXIST));

            ExpenseMember expenseMember = ExpenseMember.builder()
                    .shareAmount(memberDto.getCost())
                    .tripMember(tripMember)
                    .expense(expense)
                    .build();

            expenseMemberRepository.save(expenseMember);
        }


    }

    @Transactional
    public void updateTotalCost(Trip trip, int costDifference) {
        if (trip.getTotalCost() == null) {
            trip.setTotalCost(0);
        }
        trip.setTotalCost(trip.getTotalCost() + costDifference);
        tripRepository.save(trip);
    }

    public AllExpenseResponseDto getExpensesByTripId(Long tripId){ //전체 여행 지출 조회
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.TRIP_NOT_EXIST));

        List<Expense> expenses= expenseRepository.findByTripId(tripId); //지출 기록

        int budget = trip.getBudget(); //전체 예산
        int remainingCost = (trip.getBudget() != null ? trip.getBudget() : 0) -
                (trip.getTotalCost() != null ? trip.getTotalCost() : 0); //잔여금액

        List<TripMember> tripMembers = tripMemberRepository.findByTripId(tripId);

        List<Member> members = tripMembers.stream() //Member 형태로 변환
                .map(tripMember -> {
                    if (tripMember.getMember() == null) {
                        return Member.builder()
                                .nickname("알수없음")
                                .name("알수 없음")
                                .id(-1L)
                                .build();
                    }
                    else return tripMember.getMember();
                })
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

    @Transactional
    public void deleteMember(Long memberId){
        List<Expense> expenses = expenseRepository.findByPayerId(memberId);
        for (Expense expense : expenses) {
            expense.setPayer(null);
        }
    }

}
