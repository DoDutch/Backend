package graduation.project.DoDutch_server.domain.member.repository;

import graduation.project.DoDutch_server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {

    Member findByKakaoId(String kakaoId);

    Member findByRefreshToken(String refreshToken);

    @Query("""
        SELECT case when COUNT (m) = 0 then true else false end
        FROM Member m
        WHERE m.nickname IS NOT NULL
                and m.nickname = :nickname
        """)
    boolean validateNickname(@Param("nickname") String nickname);
}
