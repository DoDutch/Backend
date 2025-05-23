package graduation.project.DoDutch_server.domain.trip.dto;

import graduation.project.DoDutch_server.domain.expense.entity.Expense;
import graduation.project.DoDutch_server.domain.trip.dto.Request.TripRequestDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripDetailResponseDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripExpenseDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripMemberDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripResponseDTO;
import graduation.project.DoDutch_server.domain.trip.entity.Trip;
import graduation.project.DoDutch_server.domain.trip.entity.TripMember;

import java.util.List;
import java.util.stream.Collectors;

public class TripConverter {
    /*
    Dto를 Entity로 변환
     */
    public static Trip toEntity(TripRequestDTO tripRequestDTO, String joinCode /* ,String tripImageUrl*/) {
        return Trip.builder()
                .name(tripRequestDTO.getTripName())
                .place(tripRequestDTO.getPlace())
                .startDate(tripRequestDTO.getStartDate())
                .endDate(tripRequestDTO.getEndDate())
                .budget(tripRequestDTO.getBudget())
                .joinCode(joinCode)
                .totalCost(0)
                .tripImageUrl(tripRequestDTO.getTripImage()) //Todo: s3을 통해 생성된 이미지 url을 넣도록 처리
                .build();
    }

    /*
    Entity를 Dto로 변환
     */
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


    public static List<TripMemberDTO> toMemberList1(List<TripMember> tripMembers){
        return tripMembers.stream()
                .map(member -> TripMemberDTO.builder()
                        .memberId(member.getMember().getId())
                        .nickName(member.getMember().getNickname())
                        .build())
                .collect(Collectors.toList());
    }

    public static List<TripMemberDTO> toMemberList2(List<TripMember> tripMembers){
        return tripMembers.stream()
                .map(member -> TripMemberDTO.builder()
                        .memberId(member.getMember().getId())
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

    /*
    여행 정보 조회용 dto 변환
     */
    public static TripDetailResponseDTO toDetailDto(Trip trip){
        return TripDetailResponseDTO.builder()
                .tripId(trip.getId())
                .tripName(trip.getName())
                .stratDate(trip.getStartDate())
                .endDate(trip.getEndDate())
                .place(trip.getPlace())
                .totalCost(trip.getTotalCost())
                .budget(trip.getBudget())
//                .tripImageUrl(trip.getTripImageUrl())
                .members(toMemberList1(trip.getTripMembers()))
                .photos(toExpenseDtoList(trip.getExpenses()))
                .build();
    }

    /*
    여행목록 조회용 dto 변환
     */
    public static List<TripDetailResponseDTO> toDetailListDto(List<Trip> trips){
        return trips.stream()
                .map(trip -> TripDetailResponseDTO.builder()
                        .tripId(trip.getId())
                        .tripName(trip.getName())
                        .stratDate(trip.getStartDate())
                        .endDate(trip.getEndDate())
                        .place(trip.getPlace())
                        .totalCost(trip.getTotalCost())
                        .budget(trip.getBudget())
                        .members(toMemberList2(trip.getTripMembers()))
                        .build())
                .collect(Collectors.toList());
    }
}
