package java.graduation.project.DoDutch_server.global.common.apiPayload;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApiResponseStatus {
    REQUEST_SUCCESS(true, "S200", "요청에 성공했습니다.");

    private final boolean isSuccess;
    private final String status;
    private final String message;
}
