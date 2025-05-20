package graduation.project.DoDutch_server.domain.trip.repository;

import graduation.project.DoDutch_server.domain.trip.entity.TripMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripMemberRepository extends JpaRepository<TripMember, Long> {

    List<TripMember> findByTripId(Long tripId);
}
