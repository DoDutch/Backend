package graduation.project.DoDutch_server.domain.trip.repository;

import graduation.project.DoDutch_server.domain.trip.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    Optional<Trip> findByJoinCode(String joinCode);
}
