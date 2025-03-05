package twodorian.todo._core.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import twodorian.todo._core.error.ErrorCode;

public class ApiUtils {

    // 성공 응답
    public static <T> ApiResult<T> success(T response) {
        return new ApiResult<>(true, response, null);
    }

    // 예의 응답
    public static ApiResult<?> error(ErrorCode error) {
        return new ApiResult<>(false, null, new ApiError(
                error.getErrorCode(), error.getMessage(), error.getStatus().value()
        ));
    }

    @Getter
    @AllArgsConstructor
    public static class ApiResult<T> {
        private final boolean success;
        private final T response;
        private final ApiError error;
    }

    @Getter
    @AllArgsConstructor
    public static class ApiError {
        private final String errorCode;
        private final String message;
        private final int status;
    }
}