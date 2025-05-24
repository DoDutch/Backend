package graduation.project.DoDutch_server.domain.dutch.converter;

import graduation.project.DoDutch_server.domain.dutch.dto.DutchResponseDTO;
import graduation.project.DoDutch_server.domain.dutch.entity.Dutch;

public class DutchConverter {
    public static DutchResponseDTO toDutchResponseDTO(Dutch dutch) {
        return DutchResponseDTO.builder()
                .payer(dutch.getPayer().getId())
                .payee(dutch.getPayee().getId())
                .perCost(dutch.getPerCost())
                .isCompleted(dutch.getIsCompleted())
                .build();
    }
}

