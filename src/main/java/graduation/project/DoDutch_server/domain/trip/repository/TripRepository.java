package graduation.project.DoDutch_server.domain.trip.repository;

import graduation.project.DoDutch_server.domain.trip.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByNameLike(String name);

    // 여행 시작 연도와 끝나는 연도만 파싱 후 둘 중 하나라도 year와 같으면 DB에서 불러온다.
    @Query("SELECT t FROM Trip t " +
            "WHERE FUNCTION('YEAR', t.startDate) = :year " +
            "OR FUNCTION('YEAR', t.endDate) = :year")
    List<Trip> findByYear(@Param("year") Integer year);

    Optional<Trip> findByJoinCode(String joinCode);
}
