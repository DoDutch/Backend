package graduation.project.DoDutch_server.domain.dutch.converter;

import graduation.project.DoDutch_server.domain.dutch.dto.DutchResponseDTO;
import graduation.project.DoDutch_server.domain.dutch.entity.Dutch;
import graduation.project.DoDutch_server.domain.member.entity.Member;

public class DutchConverter {
    public static DutchResponseDTO toDutchResponseDTO(Dutch dutch) {
        return DutchResponseDTO.builder()
                .payer(getPayerMemberId(dutch))
                .payee(getPayeeMemberId(dutch))
                .perCost(dutch.getPerCost())
                .isCompleted(dutch.getIsCompleted())
                .build();
    }

    private static Long getPayerMemberId(Dutch dutch) {
        if (dutch.getPayer() == null) {
            return -1L;
        }
        else return dutch.getPayer().getId();

    }
    private static Long getPayeeMemberId(Dutch dutch) {
        if (dutch.getPayee() == null) {
            return -1L;
        }
        else return dutch.getPayee().getId();

    }
}

