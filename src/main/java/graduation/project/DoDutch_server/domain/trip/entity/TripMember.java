package graduation.project.DoDutch_server.domain.trip.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import graduation.project.DoDutch_server.domain.member.entity.Member;
import graduation.project.DoDutch_server.global.common.BaseEntity;

@Entity
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripMember extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "tripId", nullable = false)
    private Trip trip;
}
