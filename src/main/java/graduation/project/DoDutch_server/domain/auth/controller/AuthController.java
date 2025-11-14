package graduation.project.DoDutch_server.domain.auth.controller;

import graduation.project.DoDutch_server.domain.auth.dto.request.*;
import graduation.project.DoDutch_server.domain.auth.dto.response.KakaoResponseDTO;
import graduation.project.DoDutch_server.domain.auth.dto.response.PayPremiumApproveResponseDto;
import graduation.project.DoDutch_server.domain.auth.dto.response.PayPremiumReadyResponseDto;
import graduation.project.DoDutch_server.domain.auth.dto.response.RefreshResponseDTO;
import graduation.project.DoDutch_server.domain.auth.service.AuthService;
import graduation.project.DoDutch_server.global.common.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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

    /**
     * 모바일 앱 전용 카카오 로그인 API
     */
    @PostMapping("/kakao/login-token")
    @Operation(summary = "카카오 로그인 (모바일 SDK 액세스 토큰)", description = "모바일 앱에서 카카오 SDK로 획득한 액세스 토큰으로 로그인")
    public ApiResponse<KakaoResponseDTO> loginWithToken(@RequestBody KakaoRequestDTO kakaoRequestDTO,
                                                        HttpServletResponse response) {
        KakaoResponseDTO kakaoResponseDTO = authService.loginWithKakaoToken(kakaoRequestDTO.getAccessToken(), response);
        return ApiResponse.onSuccess(kakaoResponseDTO);
    }

    @PostMapping("/signup")
    @Operation(summary = "회원가입 API", description = "카카오 로그인 후 닉네임 설정하여 회원가입")
    public ApiResponse<Object> signup(@RequestBody SignupRequestDTO signupRequestDTO,
                                      @RequestHeader("Authorization") String authHeader) {
        String accessToken = authHeader.replace("Bearer ", "");
        authService.signup(signupRequestDTO, accessToken);
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
     */
    @PostMapping("/dev-login")
    public ApiResponse<KakaoResponseDTO> devLogin(@RequestParam String kakaoId) {
        log.info("[DEV] Development login requested for kakaoId: {}", kakaoId);
        KakaoResponseDTO response = authService.createTokensForDev(kakaoId);
        return ApiResponse.onSuccess(response);
    }

    /*
     * 닉네임 중복 확인
     */
    @PostMapping("/check-nickname")
    @Operation(summary = "닉네임 중복 확인 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<Void> checkNickname(@RequestBody NicknameRequestDto requestDto) {
        authService.checkNickname(requestDto);
        return ApiResponse.onSuccess();
    }

    @PostMapping("/premium")
    public ApiResponse<PayPremiumReadyResponseDto> premium(
            @RequestBody PayPremiumReadyRequestDto requestDto
    ) {
        PayPremiumReadyResponseDto responseDto = authService.premium(requestDto);
        return ApiResponse.onSuccess(responseDto);
    }

    @GetMapping("/approve-callback")
    public void approve(
            @RequestParam("partner_order_id") String partnerOrderId,
            @RequestParam("pg_token") String pgToken,
            HttpServletResponse response
    ) throws IOException {

        PayPremiumApproveResponseDto result = authService.approve(partnerOrderId, pgToken);

        String redirectUrl =
                "http://localhost:3000/payment/success"
                        + "?tid=" + result.tid()
                        + "&buyerId=" + result.buyerId()
                        + "&amount=" + result.amount();

        response.sendRedirect(redirectUrl);
    }
}
