package graduation.project.DoDutch_server.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import graduation.project.DoDutch_server.domain.trip.entity.TripMember;
import graduation.project.DoDutch_server.global.common.BaseEntity;
import java.util.ArrayList;
import java.util.List;

@Entity
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String nickname;

    @OneToMany(mappedBy = "member")
    private List<TripMember> tripMembers = new ArrayList<>();
}
