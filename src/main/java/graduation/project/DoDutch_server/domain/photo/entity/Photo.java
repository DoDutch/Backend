package graduation.project.DoDutch_server.domain.photo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import graduation.project.DoDutch_server.domain.expense.entity.Expense;
import graduation.project.DoDutch_server.global.common.BaseEntity;

@Entity
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Photo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String photoUrl;

    @ManyToOne
    @JoinColumn(name = "expenseId", nullable = false)
    private Expense expense;
}
