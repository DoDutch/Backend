package graduation.project.DoDutch_server.domain.trip.converter;

import graduation.project.DoDutch_server.domain.member.entity.Member;
import graduation.project.DoDutch_server.domain.trip.entity.Trip;
import graduation.project.DoDutch_server.domain.trip.entity.TripMember;

public class TripMemberConverter {

    public static TripMember toEntity(Member member, Trip trip) {
        return TripMember.builder()
                .member(member)
                .trip(trip)
                .build();
    }
}
