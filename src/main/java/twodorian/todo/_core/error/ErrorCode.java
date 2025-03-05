package twodorian.todo._core.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "2do-000", "Internal server error");

    private HttpStatus status;
    private String errorCode;
    private String message;
}
