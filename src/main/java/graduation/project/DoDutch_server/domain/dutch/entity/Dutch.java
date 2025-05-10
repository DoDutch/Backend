package graduation.project.DoDutch_server.domain.dutch.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import graduation.project.DoDutch_server.domain.member.entity.Member;
import graduation.project.DoDutch_server.domain.trip.entity.Trip;
import graduation.project.DoDutch_server.domain.trip.entity.TripMember;
import graduation.project.DoDutch_server.global.common.BaseEntity;

@Entity
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Dutch extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer perCost;
    private Boolean isCompleted;

    @ManyToOne
    @JoinColumn(name = "tripId", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "tripMemberId", nullable = false)
    private TripMember tripMember;

    @ManyToOne
    @JoinColumn(name = "payerId", nullable = true)
    private Member payer;

    @ManyToOne
    @JoinColumn(name = "payeeId", nullable = true)
    private Member payee;
}
