package graduation.project.DoDutch_server.domain.trip.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import graduation.project.DoDutch_server.domain.expense.entity.Expense;
import graduation.project.DoDutch_server.global.common.BaseEntity;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Trip extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String place;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer budget;
    private Integer totalCost;
    private String joinCode;
    private String tripImageUrl;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean dutchCompleted;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<TripMember> tripMembers = new ArrayList<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Expense> expenses = new ArrayList<>();

    public void updateInfo(String name, String place, LocalDate startDate, LocalDate endDate, Integer budget){
        if (name != null) this.name = name;
        if (place != null) this.place = place;
        if (startDate != null) this.startDate = startDate;
        if (endDate != null) this.endDate = endDate;
        if (budget != null) this.budget = budget;
    }
}
