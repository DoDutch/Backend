package graduation.project.DoDutch_server.domain.trip.Repository;

import graduation.project.DoDutch_server.domain.trip.entity.TripMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripMemberRepository extends JpaRepository<TripMember, Long> {

}
