package java.graduation.project.DoDutch_server.global.common.exception.handler;

import java.graduation.project.DoDutch_server.global.common.apiPayload.code.BaseErrorCode;
import java.graduation.project.DoDutch_server.global.common.exception.GeneralException;

public class ErrorHandler extends GeneralException {
    public ErrorHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
