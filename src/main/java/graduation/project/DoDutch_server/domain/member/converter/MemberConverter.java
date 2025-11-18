package graduation.project.DoDutch_server.domain.member.converter;

import graduation.project.DoDutch_server.domain.member.dto.response.MemberResponseDto;
import graduation.project.DoDutch_server.domain.member.entity.Member;
import graduation.project.DoDutch_server.domain.member.entity.Role;

public class MemberConverter {

    public static MemberResponseDto toMemberResponseDto(Member member){
        return new MemberResponseDto(member.getId(),
                member.getName(),
                member.getKakaoId(),
                member.getNickname(),
                member.getRole());
    }

    public static Member createNullMember(){
        return Member.builder()
                .role(Role.MEMBER)
                .name("알수없음")
                .nickname("알수없음")
                .build();
    }
}
