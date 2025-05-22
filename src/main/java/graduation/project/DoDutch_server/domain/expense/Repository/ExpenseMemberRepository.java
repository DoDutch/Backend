package graduation.project.DoDutch_server.domain.expense.Repository;

import graduation.project.DoDutch_server.domain.expense.entity.ExpenseMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseMemberRepository extends JpaRepository<ExpenseMember, Long> {
    List<ExpenseMember> findByExpenseId(Long expenseId);
}
