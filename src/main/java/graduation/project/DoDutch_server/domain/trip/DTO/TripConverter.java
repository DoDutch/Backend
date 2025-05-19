package graduation.project.DoDutch_server.domain.trip.DTO;

import graduation.project.DoDutch_server.domain.expense.entity.Expense;
import graduation.project.DoDutch_server.domain.member.entity.Member;
import graduation.project.DoDutch_server.domain.trip.DTO.Request.TripRequestDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.Response.TripDetailResponseDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.Response.TripExpenseDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.Response.TripMemberDTO;
import graduation.project.DoDutch_server.domain.trip.DTO.Response.TripResponseDTO;
import graduation.project.DoDutch_server.domain.trip.entity.Trip;
import graduation.project.DoDutch_server.domain.trip.entity.TripMember;

import java.util.List;
import java.util.stream.Collectors;

public class TripConverter {
    public static Trip toEntity(TripRequestDTO tripRequestDTO) {
        return Trip.builder()
                .name(tripRequestDTO.getName())
                .place(tripRequestDTO.getPlace())
                .startDate(tripRequestDTO.getStartDate())
                .endDate(tripRequestDTO.getEndDate())
                .budget(tripRequestDTO.getBudget())
//                .tripImageUrl() //Todo: s3Manager을 통한 이미지 처리
                .build();
    }

    public static TripResponseDTO toDto(Trip trip){
        return TripResponseDTO.builder()
                .tripImageUrl(trip.getTripImageUrl())
                .startDate(trip.getStartDate())
                .endDate(trip.getEndDate())
                .name(trip.getName())
                .place(trip.getPlace())
                .joinCode(trip.getJoinCode())
                .build();
    }

    public static List<TripResponseDTO> toDtoList(List<Trip> trips){
        return trips.stream()
                .map(TripConverter::toDto)
                .collect(Collectors.toList());
    }

    public static List<TripMemberDTO> toMemeberList(List<TripMember> tripMembers){
        return tripMembers.stream()
                .map(member -> TripMemberDTO.builder()
                        .memberId(member.getMember().getId())
                        .nickName(member.getMember().getNickname())
                        .build())
                .collect(Collectors.toList());
    }

    public static List<TripExpenseDTO> toExpenseDtoList(List<Expense> expenses){
        return expenses.stream()
                .map(expense -> TripExpenseDTO.builder()
                        .photoUrl(expense.getExpenseImageUrl())
                        .expenseDate(expense.getExpenseDate())
                        .title(expense.getTitle())
                        .amount(expense.getAmount())
                        .memo(expense.getMemo())
                        .expenseId(expense.getId())
                        .build())
                .collect(Collectors.toList());
    }

    public static TripDetailResponseDTO toDetailDto(Trip trip){
        return TripDetailResponseDTO.builder()
                .tripId(trip.getId())
                .members(toMemeberList(trip.getTripMembers()))
                .photos(toExpenseDtoList(trip.getExpenses()))
                .tripName(trip.getName())
                .totalCost(trip.getTotalCost())
                .stratDate(trip.getStartDate())
                .tripImageUrl(trip.getTripImageUrl())
                .place(trip.getPlace())
                .budget(trip.getBudget())
                .endDate(trip.getEndDate())
                .build();
    }
}
