package twodorian.todo._core.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 🔐 인증 및 권한 관련 오류
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH-001", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH-002", "접근 권한이 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-003", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-004", "만료된 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH-005", "토큰이 존재하지 않습니다."),

    // 🔍 요청 오류
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "REQ-001", "잘못된 요청입니다."),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "REQ-002", "필수 파라미터가 누락되었습니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "REQ-003", "올바르지 않은 파라미터입니다."),

    // 👤 사용자 관련 오류
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-001", "사용자를 찾을 수 없습니다."),

    // 📂 리소스 관련 오류
    NOT_FOUND(HttpStatus.NOT_FOUND, "RES-001", "요청한 리소스를 찾을 수 없습니다."),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "RES-002", "이미 존재하는 리소스입니다."),
    RESOURCE_NOT_AVAILABLE(HttpStatus.GONE, "RES-003", "리소스가 더 이상 사용 불가능합니다."),

    // ⚠️ 서버 오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SYS-001", "서버 내부 오류가 발생했습니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "SYS-002", "서비스를 이용할 수 없습니다."),
    EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, "SYS-003", "외부 API 호출 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
