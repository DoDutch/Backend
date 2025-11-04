package graduation.project.DoDutch_server.domain.member.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    MEMBER("일반"),
    PREMIUM("프리미엄");

    private final String description;
}
