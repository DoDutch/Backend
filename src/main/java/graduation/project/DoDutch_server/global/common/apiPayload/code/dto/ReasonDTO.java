package java.graduation.project.DoDutch_server.global.common.apiPayload.code.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
@AllArgsConstructor
public class ReasonDTO {
    private String message;
    private String code;
    private Boolean isSuccess;
    private HttpStatus httpStatus;
}
