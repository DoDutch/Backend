package graduation.project.DoDutch_server.domain.member.repository;

import graduation.project.DoDutch_server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
    List<Member> findByName(String name);

    Member findByKakaoId(String kakaoId);

    Member findByRefreshToken(String refreshToken);
}
