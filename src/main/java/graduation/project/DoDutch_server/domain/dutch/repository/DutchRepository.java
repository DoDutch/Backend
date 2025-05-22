package graduation.project.DoDutch_server.domain.dutch.repository;

import graduation.project.DoDutch_server.domain.dutch.entity.Dutch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DutchRepository extends JpaRepository<Dutch, Long> {
    List<Dutch> findByTripId(Long tripId);
}
