package graduation.project.DoDutch_server.domain.expense.Repository;

import graduation.project.DoDutch_server.domain.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByTripId(Long tripId);
}
