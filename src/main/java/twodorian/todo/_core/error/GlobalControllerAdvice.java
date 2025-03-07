package twodorian.todo._core.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import twodorian.todo._core.utils.ApiUtils;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<?> applicationHandler(ApplicationException e){
        log.error("ApplicationException: {} | Message: {}", e.getErrorCode().getErrorCode(), e.getMessage(), e);

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ApiUtils.error(e.getErrorCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> applicationHandler(Exception e){
        log.error("Unexpected Exception: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiUtils.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
