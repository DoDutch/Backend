package graduation.project.DoDutch_server.domain.auth.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshResponseDTO {
    private String accessToken;
    private String refreshToken;
}
