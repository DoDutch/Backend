package graduation.project.DoDutch_server.domain.member.dto.response;

import graduation.project.DoDutch_server.domain.member.entity.Role;

public record MemberResponseDto(
        Long id,
        String name,
        String email,
        String nickname,
        Role role
) {
}
