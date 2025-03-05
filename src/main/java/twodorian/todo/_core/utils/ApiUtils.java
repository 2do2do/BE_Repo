package twodorian.todo._core.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ApiUtils {

    public static <T> ApiResult<T> success(T response) {
        return new ApiResult<>(true, response, null);
    }

    public static ApiResult<?> error(String errorCode, String message, HttpStatus status) {
        return new ApiResult<>(false, null, new ApiError(errorCode, message, status.value()));
    }

    public static <T> ApiResult<T> error(T data) {
        return new ApiResult<>(false, null, data);
    }

    @Getter
    @AllArgsConstructor
    public static class ApiResult<T> {
        private final boolean success;
        private final T response;
        private final T error;
    }

    @Getter
    @AllArgsConstructor
    public static class ApiError {
        private final String errorCode;
        private final String message;
        private final int status;
    }
}