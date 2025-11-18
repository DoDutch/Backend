package graduation.project.DoDutch_server.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class KakaoRequestDTO {
    // 웹 OAuth 플로우용: authorization code
    private String accessCode;

    // 모바일 SDK용: 이미 획득한 access token
    @JsonProperty("accessToken")
    private String accessToken;

    // 실제 사용할 값 반환 (accessToken 우선, 없으면 accessCode)
    public String getAccessCode() {
        return accessCode;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
