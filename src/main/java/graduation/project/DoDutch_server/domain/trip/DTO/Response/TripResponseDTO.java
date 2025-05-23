package graduation.project.DoDutch_server.domain.trip.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripResponseDTO {
    private String tripImageUrl; // 여행 대표 이미지 url
    private LocalDate startDate; // 여행 시작 날짜
    private LocalDate endDate; // 여행 종료 날짜
    private String name; // 여행 제목
    private String place; // 여행 장소
    private String joinCode; // 여행 참여 코드
}
