package graduation.project.DoDutch_server.domain.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graduation.project.DoDutch_server.domain.auth.dto.KakaoInfoDTO;
import graduation.project.DoDutch_server.domain.auth.dto.KakaoMemberAndExistDTO;
import graduation.project.DoDutch_server.domain.auth.dto.request.RefreshRequestDTO;
import graduation.project.DoDutch_server.domain.auth.dto.request.SignupRequestDTO;
import graduation.project.DoDutch_server.domain.auth.dto.response.KakaoResponseDTO;
import graduation.project.DoDutch_server.domain.auth.dto.response.RefreshResponseDTO;
import graduation.project.DoDutch_server.domain.member.entity.Member;
import graduation.project.DoDutch_server.domain.member.entity.Role;
import graduation.project.DoDutch_server.domain.member.repository.MemberRepository;
import graduation.project.DoDutch_server.global.config.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import graduation.project.DoDutch_server.global.common.apiPayload.code.status.ErrorStatus;
import graduation.project.DoDutch_server.global.common.exception.GeneralException;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-url}")
    private String redirectUrl;

    // 카카오 로그인
    @Transactional
    public KakaoResponseDTO loginWithKakao(String accessCode, HttpServletResponse response) {
        String accessToken = getAccessToken(accessCode);

        KakaoMemberAndExistDTO kakaoMemberAndExistDTO = getUserProfileByToken(accessToken);

        Optional<Member> findMember = memberRepository.findById(kakaoMemberAndExistDTO.getMember().getId());

        return getKakaoTokens(kakaoMemberAndExistDTO.getMember().getKakaoId(), kakaoMemberAndExistDTO.isExistingMember(), response);
    }

    private String getAccessToken(String accessCode) {

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUrl);
        body.add("code", accessCode);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonNode.get("access_token").asText(); //토큰 전송
    }

    // 카카오 API 호출해서 AccessToken으로 유저정보 가져오기(id)
    public Map<String, Object> getUserAttributesByToken(String accessToken){
        return WebClient.create()
                .get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

    }

    // 카카오 API에서 가져온 유저정보를 DB에 저장
    @Transactional
    public KakaoMemberAndExistDTO getUserProfileByToken(String accessToken){
        Map<String, Object> userAttributesByToken = getUserAttributesByToken(accessToken);
        KakaoInfoDTO kakaoInfoDto = new KakaoInfoDTO(userAttributesByToken);
        Member member = Member.builder()
                .kakaoId(kakaoInfoDto.getKakaoId())
                .role(Role.MEMBER)
                .build();

        boolean existMember = false;

        if(memberRepository.findByKakaoId(member.getKakaoId()) != null) //DB에 회원정보 있으면 existMember = True
        {
            existMember = true;
        }
        else {
            memberRepository.save(member); //DB에 회원정보 없으면 저장
        }

        Member findMember = memberRepository.findByKakaoId(kakaoInfoDto.getKakaoId());
        return KakaoMemberAndExistDTO.builder()
                .member(findMember)
                .isExistingMember(existMember)
                .build();
    }

    //Access Token, Refresh Token 생성
    @Transactional
    public KakaoResponseDTO getKakaoTokens(String kakaoId, Boolean existMember, HttpServletResponse response) {
        final String accessToken = jwtTokenProvider.createAccessToken(kakaoId);
        final String refreshToken = jwtTokenProvider.createRefreshToken();

        Member member = memberRepository.findByKakaoId(kakaoId);
        member.setRefreshToken(refreshToken);


        return KakaoResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isExistingMember(existMember)
                .build();
    }

    @Transactional
    public void signup(SignupRequestDTO signUpRequestDto){
        String nickname = signUpRequestDto.getNickname();
        String accessToken = signUpRequestDto.getAccessToken();

        if(!jwtTokenProvider.validateToken(accessToken)||!StringUtils.hasText(accessToken)){
            throw new GeneralException(ErrorStatus.INVALID_ACCESS_TOKEN);
        }

        String kakaoId = jwtTokenProvider.getPayload(accessToken);

        Member member = memberRepository.findByKakaoId(kakaoId);

        member.setNickname(nickname);

    }

    // 리프레시 토큰으로 액세스토큰 새로 갱신
    public RefreshResponseDTO refreshAccessToken(RefreshRequestDTO refreshRequestDto) {

        String refreshToken = refreshRequestDto.getRefreshToken();
        //유효성 검사 실패 시
        if(!jwtTokenProvider.validateToken(refreshToken)) {
            throw new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN);
        }

        Member member = memberRepository.findByRefreshToken(refreshToken);
        if(member == null) {
            throw new GeneralException(ErrorStatus.NOT_EXIST_USER);
        }

        String changeAccessToken = jwtTokenProvider.createAccessToken(member.getKakaoId());

        return RefreshResponseDTO.builder()
                .accessToken(changeAccessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 개발 전용: 카카오 OAuth 없이 바로 토큰 생성
    @Transactional
    public KakaoResponseDTO createTokensForDev(String kakaoId) {
        // 해당 kakaoId로 회원 찾기
        Member member = memberRepository.findByKakaoId(kakaoId);

        boolean isExistingMember = true;

        // 회원이 없으면 새로 생성
        if (member == null) {
            member = Member.builder()
                    .kakaoId(kakaoId)
                    .role(Role.MEMBER)
                    .build();
            memberRepository.save(member);
            isExistingMember = false;
        }

        // JWT 토큰 생성
        final String accessToken = jwtTokenProvider.createAccessToken(kakaoId);
        final String refreshToken = jwtTokenProvider.createRefreshToken();

        // Refresh Token 저장
        member.setRefreshToken(refreshToken);

        return KakaoResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isExistingMember(isExistingMember)
                .build();
    }


}
