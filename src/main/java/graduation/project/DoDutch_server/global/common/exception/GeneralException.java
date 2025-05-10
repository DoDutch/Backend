package java.graduation.project.DoDutch_server.global.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.graduation.project.DoDutch_server.global.common.apiPayload.code.BaseErrorCode;
import java.graduation.project.DoDutch_server.global.common.apiPayload.code.dto.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {
    private BaseErrorCode code;

    public ErrorReasonDTO getErrorReason() {
        return this.code.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus() {
        return this.code.getReasonHttpStatus();
    }
}
