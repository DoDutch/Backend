package graduation.project.DoDutch_server.domain.expense.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import graduation.project.DoDutch_server.domain.trip.entity.TripMember;
import graduation.project.DoDutch_server.global.common.BaseEntity;

@Entity
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseMember extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int shareAmount;

    @ManyToOne
    @JoinColumn(name = "tripMemberId", nullable = false)
    private TripMember tripMember;

    @ManyToOne
    @JoinColumn(name = "expenseId", nullable = false)
    private Expense expense;
}
