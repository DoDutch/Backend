package graduation.project.DoDutch_server.global.common.apiPayload.code;

import graduation.project.DoDutch_server.global.common.apiPayload.code.dto.ReasonDTO;

public interface BaseCode {
    public ReasonDTO getReason();
    public ReasonDTO getReasonHttpStatus();
}
