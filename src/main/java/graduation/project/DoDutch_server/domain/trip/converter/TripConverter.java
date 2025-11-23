package graduation.project.DoDutch_server.domain.trip.converter;

import graduation.project.DoDutch_server.domain.expense.entity.Expense;
import graduation.project.DoDutch_server.domain.member.entity.Member;
import graduation.project.DoDutch_server.domain.photo.entity.Photo;
import graduation.project.DoDutch_server.domain.photo.repository.PhotoRepository;
import graduation.project.DoDutch_server.domain.trip.dto.Request.TripRequestDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripDetailResponseDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripExpenseDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripMemberDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripResponseDTO;
import graduation.project.DoDutch_server.domain.trip.entity.Trip;
import graduation.project.DoDutch_server.domain.trip.entity.TripMember;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TripConverter {
    /*
    Dto를 Entity로 변환
     */
    public static Trip toEntity(TripRequestDTO tripRequestDTO, String joinCode ,String tripImageUrl) {
        return Trip.builder()
                .name(tripRequestDTO.getTripName())
                .place(tripRequestDTO.getPlace())
                .startDate(tripRequestDTO.getStartDate())
                .endDate(tripRequestDTO.getEndDate())
                .budget(tripRequestDTO.getBudget())
                .joinCode(joinCode)
                .totalCost(0)
                .tripImageUrl(tripImageUrl)
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
        List<TripMemberDTO> dtoList = new ArrayList<>();

        for (TripMember tripMember : tripMembers){
            Member member = tripMember.getMember();
            if (member == null) dtoList.add(new TripMemberDTO(-1L, "알수없음"));
            else dtoList.add(new TripMemberDTO(member.getId(), member.getNickname()));
        }
        return dtoList;
//        return tripMembers.stream()
//                .map(member -> TripMemberDTO.builder()
//                        .memberId(member.getMember().getId())
//                        .nickName(member.getMember().getNickname())
//                        .build())
//                .collect(Collectors.toList());
    }

    public static List<TripMemberDTO> toMemberList2(List<TripMember> tripMembers){
        List<TripMemberDTO> dtoList = new ArrayList<>();

        for (TripMember tripMember : tripMembers){
            Member member = tripMember.getMember();
            if (member == null) dtoList.add(new TripMemberDTO(-1L, null));
            else dtoList.add(new TripMemberDTO(member.getId(), null));
        }
        return dtoList;
//        return tripMembers.stream()
//                .map(member -> TripMemberDTO.builder()
//                        .memberId(member.getMember().getId())
//                        .build())
//                .collect(Collectors.toList());
    }

    public static List<TripExpenseDTO> toExpenseDtoList(List<Expense> expenses, PhotoRepository photoRepository){
        return expenses.stream()
                .map(expense -> {
                    List<String> photoUrls = photoRepository.findByExpense(expense)
                            .stream()
                            .map(Photo::getPhotoUrl)
                            .collect(Collectors.toList());

                    return TripExpenseDTO.builder()
                            .expenseId(expense.getId())
                            .photoUrl(expense.getExpenseImageUrl())
                            .expensePhotoUrls(photoUrls)
                            .expenseDate(expense.getExpenseDate())
                            .title(expense.getTitle())
                            .amount(expense.getAmount())
                            .memo(expense.getMemo())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /*
    여행 정보 조회용 dto 변환
     */
    public static TripDetailResponseDTO toDetailDto(Trip trip, PhotoRepository photoRepository){
        return TripDetailResponseDTO.builder()
                .tripId(trip.getId())
                .tripName(trip.getName())
                .startDate(trip.getStartDate())
                .endDate(trip.getEndDate())
                .place(trip.getPlace())
                .totalCost(trip.getTotalCost())
                .budget(trip.getBudget())
                .tripImageUrl(trip.getTripImageUrl())
                .joinCode(trip.getJoinCode())
                .dutchCompleted(trip.getDutchCompleted())
                .members(toMemberList1(trip.getTripMembers()))
                .photos(toExpenseDtoList(trip.getExpenses(), photoRepository))
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
                        .startDate(trip.getStartDate())
                        .endDate(trip.getEndDate())
                        .place(trip.getPlace())
                        .totalCost(trip.getTotalCost())
                        .budget(trip.getBudget())
                        .joinCode(trip.getJoinCode())
                        .members(toMemberList2(trip.getTripMembers()))
                        .build())
                .collect(Collectors.toList());
    }
}
