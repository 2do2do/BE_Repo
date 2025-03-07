package twodorian.todo._core.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class ControllerLoggingAspect {

    private static final int Error_Stack_Trace_Num = 3;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("execution(* twodorian.todo..controller..*.*(..))")
    public void pointcut() {}

    @Around("pointcut()")
    public Object aroundLog(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        // 메소드 정보 추출
        Method method = getMethod(proceedingJoinPoint);
        log.info("===== API 요청: {} =====", method.getName());

        // 파라미터 추출
        logRequestParameters(proceedingJoinPoint.getArgs());
        // 시작 시간 측정
        long startTime = System.currentTimeMillis();

        try {
            // 실제 메소드 실행
            Object response = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());

            // 종료 시간 측정
            long endTime = System.currentTimeMillis();
            log.info("===== API 응답: {} | 소요 시간: {} ms =====", method.getName(), (endTime - startTime));

            logResponse(response);
            return response;
        } catch (Exception e) {
            // 종료 시간 측정
            long endTime = System.currentTimeMillis();
            log.info("===== API 응답 실패: {} | 소요 시간: {} =====", method.getName(), (endTime - startTime));
            log.error("Exception: {} | Message: {}", e.getClass().getSimpleName(), e.getMessage(), e);
            log.debug("Stack trace : {}", getShortStackTrace(e));

            throw e;
        }
    }

    // Method 정보 추출
    private Method getMethod(ProceedingJoinPoint proceedingJoinPoint) {
        return ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
    }

    // Request Parameter 로깅
    private void logRequestParameters(Object[] args) {
        if (args == null || args.length == 0) {
            log.info("No Request Parameter");
            return;
        }

        for (Object arg : args) {
            try {
                log.info("Parameter Type = {}", arg.getClass().getSimpleName());
                log.info("Parameter Value = {}", objectMapper.writeValueAsString(arg));
            } catch (JsonProcessingException e) {
                log.warn("Failed to serialize parameter: {}", arg, e);
            }
        }
    }

    // Response 로깅
    private void logResponse(Object response) {
        if (response == null) {
            log.info("Response Value = null");
            return;
        }

        log.info("Response Type = {}", response.getClass().getSimpleName());

        if (response instanceof ResponseEntity<?> responseEntity) {
            log.info("Response Body = {}", toJson(responseEntity.getBody()));
        } else {
            log.info("Response Value = {}", toJson(response));
        }
    }

    // JSON 변환
    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "Failed to serialize object";
        }
    }

    // Error Stack Trace 단축
    private String getShortStackTrace(Exception e) {
        return Arrays.stream(e.getStackTrace())
                .limit(Error_Stack_Trace_Num)
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
    }
}
