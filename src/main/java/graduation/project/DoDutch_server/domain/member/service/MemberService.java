package graduation.project.DoDutch_server.domain.member.service;

import graduation.project.DoDutch_server.domain.auth.dto.request.NicknameRequestDto;
import graduation.project.DoDutch_server.domain.auth.service.AuthService;
import graduation.project.DoDutch_server.domain.dutch.service.DutchService;
import graduation.project.DoDutch_server.domain.expense.service.ExpenseService;
import graduation.project.DoDutch_server.domain.member.converter.MemberConverter;
import graduation.project.DoDutch_server.domain.member.dto.request.MemberUpdateRequestDto;
import graduation.project.DoDutch_server.domain.member.dto.response.MemberResponseDto;
import graduation.project.DoDutch_server.domain.member.entity.Member;
import graduation.project.DoDutch_server.domain.member.repository.MemberRepository;
import graduation.project.DoDutch_server.domain.trip.service.TripServiceImpl;
import graduation.project.DoDutch_server.global.common.apiPayload.code.status.ErrorStatus;
import graduation.project.DoDutch_server.global.common.exception.handler.ErrorHandler;
import graduation.project.DoDutch_server.global.util.AuthUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthService authService;
    private final TripServiceImpl tripService;
    private final ExpenseService expenseService;
    private final DutchService dutchService;

    public MemberResponseDto getMember() {
        Member currentMember = getCurrentMember();
        return MemberConverter.toMemberResponseDto(currentMember);
    }

    public MemberResponseDto updateMember(MemberUpdateRequestDto requestDto) {
        Member member = getCurrentMember();
        String nickname = requestDto.nickname();
        authService.checkNickname(new NicknameRequestDto(nickname));
        member.setNickname(nickname);
        return MemberConverter.toMemberResponseDto(member);
    }

    private Member getCurrentMember(){
        Long currentMemberId = AuthUtils.getCurrentMemberId();
        return memberRepository.findById(currentMemberId)
                .orElseThrow(()-> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    public void deleteMember() {
        Long currentMemberId = AuthUtils.getCurrentMemberId();
//        Member nullMember = memberRepository.findByNickname("알수없음")
//                .orElse(memberRepository.save(MemberConverter.createNullMember()));
        tripService.deleteMember(currentMemberId);
        dutchService.deleteMember(currentMemberId);
        expenseService.deleteMember(currentMemberId);
        memberRepository.deleteById(currentMemberId);
    }

}
