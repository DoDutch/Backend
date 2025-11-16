package graduation.project.DoDutch_server.domain.expense.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import graduation.project.DoDutch_server.domain.member.entity.Member;
import graduation.project.DoDutch_server.domain.trip.entity.Trip;
import graduation.project.DoDutch_server.global.common.BaseEntity;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Expense extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private int amount;

    @Enumerated(EnumType.STRING)
    private ExpenseCategory expenseCategory;
    private LocalDate expenseDate;
    private String memo;
    private String expenseImageUrl;

    @ManyToOne
    @JoinColumn(name = "payer", nullable = true)
    private Member payer;

    //추가
    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false) // 외래 키 설정
    private Trip trip;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ExpenseMember> expenseMembers = new ArrayList<>();

    /**
     *
     * update 함수
     */
    public void update(String title,
                       ExpenseCategory expenseCategory,
                       int amount,
                       LocalDate expenseDate,
                       String memo,
                       Member payer) {

        this.title = title;
        this.expenseCategory = expenseCategory;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.memo = memo;
        this.payer = payer;
    }
}
