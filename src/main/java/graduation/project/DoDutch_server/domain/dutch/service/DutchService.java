package graduation.project.DoDutch_server.domain.dutch.service;

import graduation.project.DoDutch_server.domain.dutch.converter.DutchConverter;
import graduation.project.DoDutch_server.domain.dutch.dto.DutchResponseDTO;
import graduation.project.DoDutch_server.domain.dutch.dto.DutchUpdateRequestDTO;
import graduation.project.DoDutch_server.domain.dutch.entity.Dutch;
import graduation.project.DoDutch_server.domain.dutch.repository.DutchRepository;
import graduation.project.DoDutch_server.domain.expense.repository.ExpenseRepository;
import graduation.project.DoDutch_server.domain.expense.repository.ExpenseMemberRepository;
import graduation.project.DoDutch_server.domain.expense.entity.Expense;
import graduation.project.DoDutch_server.domain.expense.entity.ExpenseMember;
import graduation.project.DoDutch_server.domain.member.entity.Member;
import graduation.project.DoDutch_server.domain.member.repository.MemberRepository;
import graduation.project.DoDutch_server.domain.trip.entity.Trip;
import graduation.project.DoDutch_server.domain.trip.entity.TripMember;
import graduation.project.DoDutch_server.domain.trip.repository.TripMemberRepository;
import graduation.project.DoDutch_server.domain.trip.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static graduation.project.DoDutch_server.domain.dutch.converter.DutchConverter.toDutchResponseDTO;

@Service
@RequiredArgsConstructor
public class DutchService {
    private final DutchRepository dutchRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseMemberRepository expenseMemberRepository;
    private final TripRepository tripRepository;
    private final TripMemberRepository tripMemberRepository;
    private final MemberRepository memberRepository;

    /*
     * 정산하기
     */
    @Transactional
    public List<DutchResponseDTO> calculateDutch(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 trip입니다."));

        // 이미 정산이 완료된 경우 빈 리스트 반환
        if (Boolean.TRUE.equals(trip.getDutchCompleted())) {
            return new ArrayList<>();
        }

        // 여행에 참여한 모든 멤버 가져오기
        List<TripMember> tripMembers = tripMemberRepository.findByTripId(tripId);

        // 해당 여행에 대한 모든 Expense 내역 가져오기
        List<Expense> expenses = expenseRepository.findByTripId(tripId);

        // 각 멤버가 결제한 금액을 기록할 Map
        Map<Member, Integer> memberPayments = new HashMap<>();
        Integer totalPaidAmount = 0;

        // 각 Expense에 대해 멤버가 사용한 금액을 기록할 Map
        Map<Member, Integer> perCosts = new HashMap<>();

        // 각 Expense 내역에 대해 멤버별 사용 금액 기록하기
        for (Expense expense : expenses) {
            Member payer = expense.getPayer();
            Integer totalAmount = expense.getAmount();

            // 결제 Map에 결제자 제외 지출 멤버는 0으로 결제 금액 저장
            memberPayments.putIfAbsent(payer, 0);

            // 결제 Map에 결제자 및 결제 금액 저장
            memberPayments.put(payer, memberPayments.get(payer) + totalAmount);

            List<ExpenseMember> expenseMembers = expenseMemberRepository.findByExpenseId(expense.getId());

            // 해당 Expense에 대해 각 멤버가 사용(부담)한 금액 기록하기
            for (ExpenseMember expenseMember : expenseMembers) {
                Member member = expenseMember.getTripMember().getMember();
                Integer shareAmount = expenseMember.getShareAmount();

                perCosts.putIfAbsent(member, 0);
                perCosts.put(member, perCosts.get(member) + shareAmount);
            }
        }

        // (결제금액 - 사용금액) 계산하는 Map
        Map<Member, Integer> memberBalances = new HashMap<>();

        // 각 멤버의 차액 계산
        for (Map.Entry<Member, Integer> entry : perCosts.entrySet()) {
            Member member = entry.getKey();
            Integer totalPaid = memberPayments.getOrDefault(member, 0);     // 결제 금액
            Integer totalSpent = entry.getValue();      // 사용 금액
            int amountOwed = totalPaid - totalSpent;    // 결제 금액과 사용 금액의 차이

            // 차액 계산하기 (결제 금액에서 사용한 금액을 빼면 그 금액을 지불해야 하는지 또는 받아야 하는지 계산)
            memberBalances.put(member, amountOwed);
        }

        // 정산 결과 저장을 위한 리스트 생성
        List<Dutch> dutchEntities = new ArrayList<>();

        // 돈을 받아야 하는 멤버(+값)와 돈을 줘야 하는 멤버(-값) 나누어 다른 리스트에 저장하기
        List<Map.Entry<Member, Integer>> positiveBalances = new ArrayList<>();
        List<Map.Entry<Member, Integer>> negativeBalances = new ArrayList<>();

        // 차액이 0원이면 저장할 필요 x
        for (Map.Entry<Member, Integer> entry : memberBalances.entrySet()) {
            if (entry.getValue() > 0) {
                positiveBalances.add(entry);
            } else if (entry.getValue() < 0) {
                negativeBalances.add(entry);
            }
        }

        // 정산 계산 및 Dutch Entity 생성
        for (Map.Entry<Member, Integer> positiveEntry : positiveBalances) {
            Member payee = positiveEntry.getKey();
            Integer payeeBalance = positiveEntry.getValue();

            for (Map.Entry<Member, Integer> negativeEntry : negativeBalances) {
                if (payeeBalance <= 0) break;

                Member payer = negativeEntry.getKey();
                Integer payerBalance = negativeEntry.getValue();

                if (payerBalance < 0) {
                    Integer amountTopay = Math.min(payeeBalance, Math.abs(payerBalance));

                    // TripMember 가져오기
                    TripMember tripMember = tripMemberRepository.findByTripIdAndMemberId(trip.getId(), payer.getId())
                            .orElseThrow(() -> new IllegalArgumentException(("존재하지 않는 TripMember입니다.")));

                    // Dutch Entity 생성
                    Dutch dutch = new Dutch();
                    dutch.setTrip(trip);
                    dutch.setTripMember(tripMember);
                    dutch.setPayer(payer);
                    dutch.setPayee(payee);
                    dutch.setPerCost(amountTopay);
                    dutch.setIsCompleted(false);
                    dutchEntities.add(dutch);

                    // 잔액 갱신
                    payeeBalance -= amountTopay;
                    payerBalance += amountTopay;

                    memberBalances.put(payee, payeeBalance);
                    memberBalances.put(payer, payerBalance);
                }
            }
        }

        List<Dutch> savedDutches = dutchRepository.saveAll(dutchEntities);

        trip.setDutchCompleted(true);
        tripRepository.save(trip);

        List<DutchResponseDTO> dutchResponseDTOs = savedDutches.stream()
                .map(DutchConverter::toDutchResponseDTO)
                .collect(Collectors.toList());

        return dutchResponseDTOs;
    }


    /*
     * 정산 목록 조회
     */
    public List<DutchResponseDTO> findAllDutchs(Long tripId) {
        return dutchRepository.findByTripId(tripId)
                .stream()
                .map(DutchConverter::toDutchResponseDTO)
                .collect(Collectors.toList());
    }

    /*
     * 정산 세부 내역 조회
     */
    public DutchResponseDTO findDutchById(Long tripId, Long dutchId) {
        Dutch dutch = dutchRepository.findById(dutchId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 dutch입니다."));
        return toDutchResponseDTO(dutch);
    }


    /*
     * 정산 여부 완료 표기
     */
    @Transactional
    public DutchResponseDTO updateDutch(Long tripId, Long dutchId, DutchUpdateRequestDTO dutchUpdateRequestDTO) {
        Dutch dutch = dutchRepository.findById(dutchId)
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 dutch입니다."));
        dutch.setIsCompleted(dutchUpdateRequestDTO.getIsCompleted());
        dutchRepository.save(dutch);
        return toDutchResponseDTO(dutch);
    }

    @Transactional
    public void deleteMember(Long memberId){
        List<Dutch> dutches = dutchRepository.findByPayerId(memberId);
        for (Dutch dutch : dutches) {
            dutch.setPayer(null);
        }
        dutches = dutchRepository.findByPayeeId(memberId);
        for (Dutch dutch : dutches) {
            dutch.setPayee(null);
        }
    }
}
