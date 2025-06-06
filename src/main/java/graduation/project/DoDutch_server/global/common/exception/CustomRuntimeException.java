package graduation.project.DoDutch_server.global.common.exception;

import org.springframework.http.HttpStatus;

public class CustomRuntimeException extends RuntimeException {
    private final HttpStatus status;

    public CustomRuntimeException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
