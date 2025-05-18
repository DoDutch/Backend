package graduation.project.DoDutch_server.domain.trip.Repository;

import graduation.project.DoDutch_server.domain.trip.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {

}
