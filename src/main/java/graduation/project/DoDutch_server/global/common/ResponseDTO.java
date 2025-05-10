package graduation.project.DoDutch_server.global.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDTO<T> {
    private boolean isSuccess;
    private String code;
    private String message;
    private T data;
}
