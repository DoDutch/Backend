package graduation.project.DoDutch_server.domain.dutch.converter;

import graduation.project.DoDutch_server.domain.dutch.dto.DutchResponseDTO;
import graduation.project.DoDutch_server.domain.dutch.entity.Dutch;
import graduation.project.DoDutch_server.domain.member.entity.Member;

public class DutchConverter {
    public static DutchResponseDTO toDutchResponseDTO(Dutch dutch) {
        return DutchResponseDTO.builder()
                .dutchId(dutch.getId())
                .payer(DutchResponseDTO.MemberInfo.builder()
                        .memberId(dutch.getPayer().getId())
                        .nickname(dutch.getPayer().getNickname())
                        .build())
                .payee(DutchResponseDTO.MemberInfo.builder()
                        .memberId(dutch.getPayee().getId())
                        .nickname(dutch.getPayee().getNickname())
                        .build())
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

