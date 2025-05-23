package graduation.project.DoDutch_server.domain.expense.repository;

import graduation.project.DoDutch_server.domain.expense.entity.ExpenseMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseMemberRepository extends JpaRepository<ExpenseMember , Long> {

}
