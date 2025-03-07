package twodorian.todo._core.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import twodorian.todo._core.error.ApplicationException;
import twodorian.todo._core.error.ErrorCode;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE) // 객체 생성 방지
public class SecurityUtils {

    public static Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return Optional.ofNullable(authentication)
                .filter(auth -> auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()))
                .map(auth -> Long.parseLong(auth.getName()))
                .orElseThrow(() -> new ApplicationException(ErrorCode.UNAUTHORIZED));
    }
}
