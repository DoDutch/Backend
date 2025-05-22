package graduation.project.DoDutch_server.domain.trip.Repository;

import graduation.project.DoDutch_server.domain.trip.entity.Trip;
import graduation.project.DoDutch_server.domain.trip.entity.TripMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByNameLike(String name);

    @Query("SELECT t FROM Trip t " +
            "WHERE FUNCTION('YEAR', t.startDate) = :year " +
            "OR FUNCTION('YEAR', t.endDate) = :year")
    List<Trip> findByYear(@Param("year") Integer year);

    Optional<Trip> findByJoinCode(String joinCode);
}
