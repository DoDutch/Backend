package graduation.project.DoDutch_server.domain.trip.Repository;

import graduation.project.DoDutch_server.domain.trip.entity.Trip;
import graduation.project.DoDutch_server.domain.trip.entity.TripMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByNameOrDateOrTripMembers(String name, LocalDate date, TripMember tripMembers);
    //Todo: 여행이름, 연도, 여행 구성원으로 검색 가능한 함수 만들기
}
