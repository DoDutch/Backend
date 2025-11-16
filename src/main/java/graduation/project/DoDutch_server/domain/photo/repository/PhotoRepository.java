package graduation.project.DoDutch_server.domain.photo.repository;

import graduation.project.DoDutch_server.domain.expense.entity.Expense;
import graduation.project.DoDutch_server.domain.photo.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo,Long> {
    List<Photo> findByExpenseId(Long expenseId);
    List<Photo> findByExpense(Expense expense);
}
