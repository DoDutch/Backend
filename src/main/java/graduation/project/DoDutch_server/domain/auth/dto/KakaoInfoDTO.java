package graduation.project.DoDutch_server.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class KakaoInfoDTO {
    private String kakaoId;

    public KakaoInfoDTO(Map<String, Object> attributes) {
        this.kakaoId = attributes.get("id").toString();
    }
}
