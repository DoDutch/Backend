package graduation.project.DoDutch_server.domain.member.repository;

import graduation.project.DoDutch_server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
