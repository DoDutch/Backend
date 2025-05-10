package graduation.project.DoDutch_server.global.common.apiPayload.code;

import graduation.project.DoDutch_server.global.common.apiPayload.code.dto.ErrorReasonDTO;

public interface BaseErrorCode {
    public ErrorReasonDTO getReason();
    public ErrorReasonDTO getReasonHttpStatus();
}
