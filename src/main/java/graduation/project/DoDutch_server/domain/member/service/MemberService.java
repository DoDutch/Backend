package graduation.project.DoDutch_server.domain.member.service;

import graduation.project.DoDutch_server.domain.member.converter.MemberConverter;
import graduation.project.DoDutch_server.domain.member.dto.response.MemberResponseDto;
import graduation.project.DoDutch_server.domain.member.entity.Member;
import graduation.project.DoDutch_server.domain.member.repository.MemberRepository;
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

    public MemberResponseDto getMember() {
        Long currentMemberId = AuthUtils.getCurrentMemberId();
        Member currentMember = memberRepository.findById(currentMemberId)
                .orElseThrow(()-> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        return MemberConverter.toMemberResponseDto(currentMember);
    }

//    public void deleteMember() {
//        Long currentMemberId = AuthUtils.getCurrentMemberId();
//        memberRepository.deleteById(currentMemberId);
//    }

}
