package graduation.project.DoDutch_server.global.common.exception.handler;

import graduation.project.DoDutch_server.global.common.apiPayload.code.BaseErrorCode;
import graduation.project.DoDutch_server.global.common.exception.GeneralException;

public class ErrorHandler extends GeneralException {
    public ErrorHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
