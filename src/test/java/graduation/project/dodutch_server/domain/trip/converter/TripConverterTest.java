package graduation.project.DoDutch_server.domain.trip.converter;

import graduation.project.DoDutch_server.domain.expense.entity.Expense;
import graduation.project.DoDutch_server.domain.member.entity.Member;
import graduation.project.DoDutch_server.domain.photo.entity.Photo;
import graduation.project.DoDutch_server.domain.photo.repository.PhotoRepository;
import graduation.project.DoDutch_server.domain.trip.dto.Request.TripRequestDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripDetailResponseDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripExpenseDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripMemberDTO;
import graduation.project.DoDutch_server.domain.trip.dto.Response.TripResponseDTO;
import graduation.project.DoDutch_server.domain.trip.entity.Trip;
import graduation.project.DoDutch_server.domain.trip.entity.TripMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("TripConverter 단위 테스트")
class TripConverterTest {

    @Mock
    private PhotoRepository photoRepository;

    // ─── TC-1 ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("정상 요청 → Trip 엔티티 변환")
    void should_returnTrip_when_validRequest() {
        TripRequestDTO dto = TripRequestDTO.builder()
                .tripName("도쿄여행")
                .place("일본")
                .startDate(LocalDate.of(2026, 5, 1))
                .endDate(LocalDate.of(2026, 5, 7))
                .budget(500000)
                .build();

        Trip trip = TripConverter.toEntity(dto, "ABC123", "https://example.com/img.jpg");

        assertThat(trip.getName()).isEqualTo("도쿄여행");
        assertThat(trip.getPlace()).isEqualTo("일본");
        assertThat(trip.getStartDate()).isEqualTo(LocalDate.of(2026, 5, 1));
        assertThat(trip.getEndDate()).isEqualTo(LocalDate.of(2026, 5, 7));
        assertThat(trip.getBudget()).isEqualTo(500000);
        assertThat(trip.getJoinCode()).isEqualTo("ABC123");
        assertThat(trip.getTripImageUrl()).isEqualTo("https://example.com/img.jpg");
        assertThat(trip.getTotalCost()).isEqualTo(0);
    }

    // ─── TC-2 ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("tripImageUrl이 null → Trip 엔티티 변환 (이미지 없는 경우)")
    void should_returnTrip_when_tripImageUrlIsNull() {
        TripRequestDTO dto = TripRequestDTO.builder()
                .tripName("도쿄여행")
                .place("일본")
                .startDate(LocalDate.of(2026, 5, 1))
                .endDate(LocalDate.of(2026, 5, 7))
                .budget(500000)
                .build();

        Trip trip = TripConverter.toEntity(dto, "XYZ", null);

        assertThat(trip.getTripImageUrl()).isNull();
        assertThat(trip.getName()).isEqualTo("도쿄여행");
        assertThat(trip.getJoinCode()).isEqualTo("XYZ");
        assertThat(trip.getTotalCost()).isEqualTo(0);
    }

    // ─── TC-3 ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Trip 엔티티 → TripResponseDTO 변환")
    void should_returnTripResponseDTO_when_tripEntity() {
        Trip trip = Trip.builder()
                .name("제주여행")
                .place("제주도")
                .startDate(LocalDate.of(2026, 6, 1))
                .endDate(LocalDate.of(2026, 6, 5))
                .joinCode("JJ999")
                .tripImageUrl("https://s3/jeju.jpg")
                .tripMembers(new ArrayList<>())
                .expenses(new ArrayList<>())
                .build();

        TripResponseDTO dto = TripConverter.toDto(trip);

        assertThat(dto.getName()).isEqualTo("제주여행");
        assertThat(dto.getPlace()).isEqualTo("제주도");
        assertThat(dto.getJoinCode()).isEqualTo("JJ999");
        assertThat(dto.getTripImageUrl()).isEqualTo("https://s3/jeju.jpg");
        assertThat(dto.getStartDate()).isEqualTo(LocalDate.of(2026, 6, 1));
        assertThat(dto.getEndDate()).isEqualTo(LocalDate.of(2026, 6, 5));
    }

    // ─── TC-4 ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("모든 멤버가 유효한 경우 toMemberList1")
    void should_returnMemberList1_when_allMembersValid() {
        Member member1 = Member.builder().id(1L).nickname("홍길동").build();
        Member member2 = Member.builder().id(2L).nickname("김영희").build();

        TripMember tm1 = TripMember.builder().member(member1).build();
        TripMember tm2 = TripMember.builder().member(member2).build();

        List<TripMemberDTO> result = TripConverter.toMemberList1(List.of(tm1, tm2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMemberId()).isEqualTo(1L);
        assertThat(result.get(0).getNickname()).isEqualTo("홍길동");
        assertThat(result.get(1).getMemberId()).isEqualTo(2L);
        assertThat(result.get(1).getNickname()).isEqualTo("김영희");
    }

    // ─── TC-5 ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("member가 null인 탈퇴 멤버 포함 시 toMemberList1 — 알수없음 처리")
    void should_returnUnknownMember_when_memberIsNull_inMemberList1() {
        TripMember tmNull = TripMember.builder().member(null).build();
        Member member3 = Member.builder().id(3L).nickname("이철수").build();
        TripMember tm3 = TripMember.builder().member(member3).build();

        List<TripMemberDTO> result = TripConverter.toMemberList1(List.of(tmNull, tm3));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMemberId()).isEqualTo(-1L);
        assertThat(result.get(0).getNickname()).isEqualTo("알수없음");
        assertThat(result.get(1).getMemberId()).isEqualTo(3L);
        assertThat(result.get(1).getNickname()).isEqualTo("이철수");
    }

    // ─── TC-6 ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("빈 리스트 입력 시 toMemberList1 → 빈 리스트 반환")
    void should_returnEmptyList_when_emptyInput_toMemberList1() {
        List<TripMemberDTO> result = TripConverter.toMemberList1(new ArrayList<>());

        assertThat(result).isEmpty();
    }

    // ─── TC-7 ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("모든 멤버가 유효한 경우 toMemberList2 — nickname은 항상 null")
    void should_returnMemberList2WithNullNickname_when_allMembersValid() {
        Member member1 = Member.builder().id(1L).nickname("홍길동").build();
        Member member2 = Member.builder().id(2L).nickname("김영희").build();

        TripMember tm1 = TripMember.builder().member(member1).build();
        TripMember tm2 = TripMember.builder().member(member2).build();

        List<TripMemberDTO> result = TripConverter.toMemberList2(List.of(tm1, tm2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMemberId()).isEqualTo(1L);
        assertThat(result.get(0).getNickname()).isNull();
        assertThat(result.get(1).getMemberId()).isEqualTo(2L);
        assertThat(result.get(1).getNickname()).isNull();
    }

    // ─── TC-8 ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("member가 null인 탈퇴 멤버 포함 시 toMemberList2 — id=-1, nickname=null")
    void should_returnUnknownMember_when_memberIsNull_inMemberList2() {
        TripMember tmNull = TripMember.builder().member(null).build();

        List<TripMemberDTO> result = TripConverter.toMemberList2(List.of(tmNull));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMemberId()).isEqualTo(-1L);
        assertThat(result.get(0).getNickname()).isNull();
    }

    // ─── TC-9 ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Expense 목록 변환 — 각 Expense에 사진 있음")
    void should_returnExpenseDtoList_when_expensesHavePhotos() {
        Expense expense1 = Expense.builder()
                .id(1L)
                .title("식사")
                .amount(50000)
                .memo("저녁 식사")
                .expenseDate(LocalDate.of(2026, 5, 2))
                .expenseImageUrl("https://s3/img1.jpg")
                .build();
        Expense expense2 = Expense.builder()
                .id(2L)
                .title("교통")
                .amount(30000)
                .memo("지하철")
                .expenseDate(LocalDate.of(2026, 5, 3))
                .expenseImageUrl("https://s3/img2.jpg")
                .build();

        Photo photo1 = Photo.builder().photoUrl("url1").build();
        Photo photo2 = Photo.builder().photoUrl("url2").build();
        Photo photo3 = Photo.builder().photoUrl("url3").build();

        given(photoRepository.findByExpense(expense1)).willReturn(List.of(photo1));
        given(photoRepository.findByExpense(expense2)).willReturn(List.of(photo2, photo3));

        List<TripExpenseDTO> result = TripConverter.toExpenseDtoList(List.of(expense1, expense2), photoRepository);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getExpenseId()).isEqualTo(1L);
        assertThat(result.get(0).getTitle()).isEqualTo("식사");
        assertThat(result.get(0).getAmount()).isEqualTo(50000);
        assertThat(result.get(0).getMemo()).isEqualTo("저녁 식사");
        assertThat(result.get(0).getExpenseDate()).isEqualTo(LocalDate.of(2026, 5, 2));
        assertThat(result.get(0).getPhotoUrl()).isEqualTo("https://s3/img1.jpg");
        assertThat(result.get(0).getExpensePhotoUrls()).containsExactly("url1");
        assertThat(result.get(1).getExpensePhotoUrls()).containsExactly("url2", "url3");
    }

    // ─── TC-10 ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Expense에 사진이 없는 경우 → expensePhotoUrls 빈 리스트")
    void should_returnEmptyPhotoUrls_when_expenseHasNoPhotos() {
        Expense expense = Expense.builder()
                .id(1L)
                .title("식사")
                .amount(50000)
                .build();

        given(photoRepository.findByExpense(expense)).willReturn(Collections.emptyList());

        List<TripExpenseDTO> result = TripConverter.toExpenseDtoList(List.of(expense), photoRepository);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getExpensePhotoUrls()).isEmpty();
    }

    // ─── TC-11 ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("빈 Expense 목록 → 빈 리스트 반환, photoRepository 미호출")
    void should_returnEmptyList_when_emptyExpenses() {
        List<TripExpenseDTO> result = TripConverter.toExpenseDtoList(new ArrayList<>(), photoRepository);

        assertThat(result).isEmpty();
        verify(photoRepository, never()).findByExpense(any(Expense.class));
    }

    // ─── TC-12 ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Trip 상세 조회 변환 — 정상 케이스")
    void should_returnDetailDto_when_validTrip() {
        Member member1 = Member.builder().id(1L).nickname("홍길동").build();
        Member member2 = Member.builder().id(2L).nickname("김영희").build();
        TripMember tm1 = TripMember.builder().member(member1).build();
        TripMember tm2 = TripMember.builder().member(member2).build();

        Expense expense = Expense.builder()
                .id(1L)
                .title("식사")
                .amount(30000)
                .expenseDate(LocalDate.of(2026, 5, 2))
                .build();

        Trip trip = Trip.builder()
                .id(10L)
                .name("파리여행")
                .place("프랑스")
                .startDate(LocalDate.of(2026, 7, 1))
                .endDate(LocalDate.of(2026, 7, 10))
                .budget(2000000)
                .totalCost(30000)
                .joinCode("PARIS1")
                .tripImageUrl("https://s3/paris.jpg")
                .dutchCompleted(false)
                .tripMembers(new ArrayList<>(List.of(tm1, tm2)))
                .expenses(new ArrayList<>(List.of(expense)))
                .build();

        given(photoRepository.findByExpense(expense)).willReturn(Collections.emptyList());

        TripDetailResponseDTO dto = TripConverter.toDetailDto(trip, photoRepository);

        assertThat(dto.getTripId()).isEqualTo(10L);
        assertThat(dto.getTripName()).isEqualTo("파리여행");
        assertThat(dto.getPlace()).isEqualTo("프랑스");
        assertThat(dto.getStartDate()).isEqualTo(LocalDate.of(2026, 7, 1));
        assertThat(dto.getEndDate()).isEqualTo(LocalDate.of(2026, 7, 10));
        assertThat(dto.getBudget()).isEqualTo(2000000);
        assertThat(dto.getTotalCost()).isEqualTo(30000);
        assertThat(dto.getJoinCode()).isEqualTo("PARIS1");
        assertThat(dto.getTripImageUrl()).isEqualTo("https://s3/paris.jpg");
        assertThat(dto.getDutchCompleted()).isFalse();
        assertThat(dto.getMembers()).hasSize(2);
        assertThat(dto.getPhotos()).hasSize(1);
    }

    // ─── TC-13 ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("탈퇴 멤버 포함 시 toDetailDto — 알수없음 처리 위임 확인")
    void should_returnUnknownMember_when_nullMemberInDetailDto() {
        TripMember tmNull = TripMember.builder().member(null).build();
        Member member = Member.builder().id(5L).nickname("정상멤버").build();
        TripMember tm = TripMember.builder().member(member).build();

        Trip trip = Trip.builder()
                .id(1L)
                .name("여행")
                .place("서울")
                .startDate(LocalDate.of(2026, 5, 1))
                .endDate(LocalDate.of(2026, 5, 5))
                .budget(100000)
                .totalCost(0)
                .joinCode("CODE1")
                .dutchCompleted(false)
                .tripMembers(new ArrayList<>(List.of(tmNull, tm)))
                .expenses(new ArrayList<>())
                .build();

        TripDetailResponseDTO dto = TripConverter.toDetailDto(trip, photoRepository);

        assertThat(dto.getMembers()).hasSize(2);
        assertThat(dto.getMembers().get(0).getMemberId()).isEqualTo(-1L);
        assertThat(dto.getMembers().get(0).getNickname()).isEqualTo("알수없음");
        assertThat(dto.getMembers().get(1).getMemberId()).isEqualTo(5L);
        assertThat(dto.getMembers().get(1).getNickname()).isEqualTo("정상멤버");
    }

    // ─── TC-14 ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Trip 목록 변환 — 복수 여행")
    void should_returnDetailListDto_when_multipleTrips() {
        Member member1 = Member.builder().id(1L).nickname("홍길동").build();
        TripMember tm1 = TripMember.builder().member(member1).build();

        Trip trip1 = Trip.builder()
                .id(1L)
                .name("도쿄여행")
                .place("일본")
                .startDate(LocalDate.of(2026, 5, 1))
                .endDate(LocalDate.of(2026, 5, 7))
                .budget(500000)
                .totalCost(100000)
                .joinCode("TOKYO1")
                .tripMembers(new ArrayList<>(List.of(tm1)))
                .expenses(new ArrayList<>())
                .build();

        Member member2 = Member.builder().id(2L).nickname("김영희").build();
        TripMember tm2 = TripMember.builder().member(member2).build();

        Trip trip2 = Trip.builder()
                .id(2L)
                .name("제주여행")
                .place("제주도")
                .startDate(LocalDate.of(2026, 6, 1))
                .endDate(LocalDate.of(2026, 6, 5))
                .budget(300000)
                .totalCost(50000)
                .joinCode("JEJU1")
                .tripMembers(new ArrayList<>(List.of(tm2)))
                .expenses(new ArrayList<>())
                .build();

        List<TripDetailResponseDTO> result = TripConverter.toDetailListDto(List.of(trip1, trip2));

        assertThat(result).hasSize(2);

        TripDetailResponseDTO dto1 = result.get(0);
        assertThat(dto1.getTripId()).isEqualTo(1L);
        assertThat(dto1.getTripName()).isEqualTo("도쿄여행");
        assertThat(dto1.getPlace()).isEqualTo("일본");
        assertThat(dto1.getStartDate()).isEqualTo(LocalDate.of(2026, 5, 1));
        assertThat(dto1.getEndDate()).isEqualTo(LocalDate.of(2026, 5, 7));
        assertThat(dto1.getBudget()).isEqualTo(500000);
        assertThat(dto1.getTotalCost()).isEqualTo(100000);
        assertThat(dto1.getJoinCode()).isEqualTo("TOKYO1");
        assertThat(dto1.getMembers()).hasSize(1);
        assertThat(dto1.getMembers().get(0).getMemberId()).isEqualTo(1L);
        assertThat(dto1.getMembers().get(0).getNickname()).isNull();
        assertThat(dto1.getTripImageUrl()).isNull();
        assertThat(dto1.getDutchCompleted()).isNull();
        assertThat(dto1.getPhotos()).isNull();

        TripDetailResponseDTO dto2 = result.get(1);
        assertThat(dto2.getTripId()).isEqualTo(2L);
        assertThat(dto2.getTripName()).isEqualTo("제주여행");
        assertThat(dto2.getPlace()).isEqualTo("제주도");
        assertThat(dto2.getMembers()).hasSize(1);
        assertThat(dto2.getMembers().get(0).getMemberId()).isEqualTo(2L);
        assertThat(dto2.getMembers().get(0).getNickname()).isNull();
    }

    // ─── TC-15 ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("빈 목록 입력 시 toDetailListDto → 빈 리스트 반환")
    void should_returnEmptyList_when_emptyTripList() {
        List<TripDetailResponseDTO> result = TripConverter.toDetailListDto(new ArrayList<>());

        assertThat(result).isEmpty();
    }
}
