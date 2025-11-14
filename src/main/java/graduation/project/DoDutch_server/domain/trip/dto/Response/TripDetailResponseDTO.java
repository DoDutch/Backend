package graduation.project.DoDutch_server.domain.trip.dto.Response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TripDetailResponseDTO {
    private Long tripId;
    private String tripName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String place;
    private Integer totalCost;
    private Integer budget;
    private String tripImageUrl;
    private List<TripMemberDTO> members;
    private List<TripExpenseDTO> photos;

}
