package graduation.project.DoDutch_server.domain.member.converter;

import graduation.project.DoDutch_server.domain.member.dto.response.MemberResponseDto;
import graduation.project.DoDutch_server.domain.member.entity.Member;

public class MemberConverter {

    public static MemberResponseDto toMemberResponseDto(Member member){
        return new MemberResponseDto(member.getId(),
                member.getName(),
                member.getKakaoId(),
                member.getNickname(),
                member.getRole());
    }
}
