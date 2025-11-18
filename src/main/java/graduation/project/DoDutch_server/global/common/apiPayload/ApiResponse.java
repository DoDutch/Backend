package graduation.project.DoDutch_server.global.common.apiPayload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import graduation.project.DoDutch_server.global.common.apiPayload.code.BaseCode;
import graduation.project.DoDutch_server.global.common.apiPayload.code.status.SuccessStatus;

@Getter
@NoArgsConstructor
@JsonPropertyOrder({"success", "code", "message", "data"}) // 변수 순서 지정
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    /*
     * 성공한 경우 응답 생성
     */

    // 1. 데이터가 있는 성공 응답 반환
    public static <T> ApiResponse<T> onSuccess(T result) {
        return new ApiResponse<>(true, SuccessStatus._OK.getCode(), SuccessStatus._OK.getMessage(), result);
    }

    // 2. 데이터가 없는 성공 응답 반환 (ex: 단순 POST 성공, DELETE 성공)
    public static <T> ApiResponse<T> onSuccess() {
        return new ApiResponse<>(true, SuccessStatus._OK.getCode(), SuccessStatus._OK.getMessage(), null);
    }

    // 3. BaseCode의 코드, 메시지를 사용하여 성공 응답 반환
    public static <T> ApiResponse<T> of(BaseCode code, T result) {
        return new ApiResponse<>(true, code.getReasonHttpStatus().getCode(), code.getReasonHttpStatus().getMessage(), result);
    }

    // 실패한 경우 응답 생성
    public static <T> ApiResponse<T> onFailure(String code, String message, T data) {
        return new ApiResponse<>(false, code, message, data);
    }
}
