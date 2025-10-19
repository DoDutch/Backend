package graduation.project.DoDutch_server.global.util;

import graduation.project.DoDutch_server.domain.auth.model.UserPrincipal;
import graduation.project.DoDutch_server.domain.member.entity.Member;
import graduation.project.DoDutch_server.domain.member.repository.MemberRepository;
import graduation.project.DoDutch_server.global.common.apiPayload.code.status.ErrorStatus;
import graduation.project.DoDutch_server.global.common.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 인증 관련 유틸리티 클래스
 * 현재 로그인한 사용자의 정보를 쉽게 가져올 수 있는 헬퍼 메서드 제공
 */
@Component
@RequiredArgsConstructor
public class AuthUtils {

    private final MemberRepository memberRepository;

    // 현재 로그인한 사용자의 UserPrincipal 가져오기
    public static UserPrincipal getCurrentUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserPrincipal)) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }

        return (UserPrincipal) principal;
    }

    // 현재 로그인한 사용자의 Member 엔티티 가져오기
    public Member getCurrentMember() {
        UserPrincipal userPrincipal = getCurrentUserPrincipal();

        return memberRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    // 현재 로그인한 사용자의 ID 가져오기
    public static Long getCurrentMemberId() {
        return getCurrentUserPrincipal().getId();
    }

    // 현재 로그인한 사용자의 KakaoId 가져오기
    public static String getCurrentKakaoId() {
        return getCurrentUserPrincipal().getSocialId();
    }

    // 특정 Member가 현재 로그인한 사용자인지 확인
    public static boolean isCurrentMember(Long memberId) {
        return getCurrentMemberId().equals(memberId);
    }
}