package graduation.project.DoDutch_server.domain.auth.controller;

import graduation.project.DoDutch_server.domain.auth.dto.request.KakaoRequestDTO;
import graduation.project.DoDutch_server.domain.auth.dto.request.RefreshRequestDTO;
import graduation.project.DoDutch_server.domain.auth.dto.request.SignupRequestDTO;
import graduation.project.DoDutch_server.domain.auth.dto.response.KakaoResponseDTO;
import graduation.project.DoDutch_server.domain.auth.dto.response.RefreshResponseDTO;
import graduation.project.DoDutch_server.domain.auth.service.AuthService;
import graduation.project.DoDutch_server.global.common.apiPayload.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Profile;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/kakao/login")
    public ApiResponse<KakaoResponseDTO> login(@RequestBody KakaoRequestDTO kakaoRequestDTO,
                                               HttpServletResponse response) {
        KakaoResponseDTO kakaoResponseDTO = authService.loginWithKakao(kakaoRequestDTO.getAccessCode(), response);
        return ApiResponse.onSuccess(kakaoResponseDTO);
    }

    @PostMapping("/signup")
    public ApiResponse<Object> signup(@RequestBody SignupRequestDTO signupRequestDTO) {

        authService.signup(signupRequestDTO);
        return ApiResponse.onSuccess();
    }

    @PostMapping("/refresh-token")
    public ApiResponse<RefreshResponseDTO> refreshToken(@RequestBody RefreshRequestDTO refreshRequestDTO,
                                                     HttpServletResponse response) {
        RefreshResponseDTO refreshResponseDTO = authService.refreshAccessToken(refreshRequestDTO);
        return ApiResponse.onSuccess(refreshResponseDTO);
    }

    /**
     * 개발 전용 로그인 API
     * 카카오 OAuth 과정 없이 kakaoId로 바로 JWT 토큰 발급
     * 프로덕션 환경에서는 비활성화됨
     */
    @PostMapping("/dev-login")
    public ApiResponse<KakaoResponseDTO> devLogin(@RequestParam String kakaoId) {
        log.info("[DEV] Development login requested for kakaoId: {}", kakaoId);
        KakaoResponseDTO response = authService.createTokensForDev(kakaoId);
        return ApiResponse.onSuccess(response);
    }
}
