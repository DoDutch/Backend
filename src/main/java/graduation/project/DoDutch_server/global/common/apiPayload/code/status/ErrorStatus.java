package graduation.project.DoDutch_server.global.common.apiPayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import graduation.project.DoDutch_server.global.common.apiPayload.code.BaseErrorCode;
import graduation.project.DoDutch_server.global.common.apiPayload.code.dto.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 멤버 관련 응답
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4001", "멤버가 없습니다."),
    MEMBER_NICKNAME_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "중복된 닉네임입니다"),

    // 지출 관련 응답
    EXPENSE_NOT_EXIST(HttpStatus.NOT_FOUND, "EXPENSE4001", "존재하지 않는 지출입니다."),

    // 정산 관련 응답
    DUTCH_NOT_EXIST(HttpStatus.NOT_FOUND, "DUTCH4001", "존재하지 않는 정산입니다."),
    DUTCH_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "DUTCH4002", "이미 처리된 정산입니다."),

    // 인증 관련 응답

    // 기타
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND,"CATEGORY4001", "카테고리가 없습니다."),

    // 여행 관련 응답
    TRIP_NOT_EXIST(HttpStatus.NOT_FOUND, "TRIP4001", "존재하지 않는 여행입니다."),
    TRIP_MEMBER_EXIST(HttpStatus.BAD_REQUEST, "TRIP4002", "이미 존재하는 여행 멤버입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
