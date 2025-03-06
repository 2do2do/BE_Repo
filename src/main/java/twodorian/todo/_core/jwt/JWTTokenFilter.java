package twodorian.todo._core.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import twodorian.todo._core.error.ApplicationException;
import twodorian.todo._core.error.ErrorCode;

import java.io.IOException;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class JWTTokenFilter extends GenericFilterBean {

    private final JWTTokenProvider jwtTokenProvider;
    private static final Set<String> EXCLUDE_URLS = Set.of("/api/auth/reissue", "/api/auth/logout");

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        // Request Header 에서 JWT Token 추출
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) servletRequest);

        // 토큰 유효성 검사
        /*
            토큰 재발급 요청 시
            refresh token 을 요청 headers 에 Authorization 키로 받으면
            token 이 존재하기 때문에
            getAuthentication 메소드가 동작
            -> 내부적으로 권한에 대한 정보가 없기 때문에 Exception 발생
            => 토큰 재발급 요청 path 인 경우 필터 패스
         */

        if (token != null) {
            if (!jwtTokenProvider.validateToken(token)) {
                throw new ApplicationException(ErrorCode.INVALID_TOKEN);
            }
            if (!EXCLUDE_URLS.contains(request.getRequestURI())) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("{} 님이 로그인 하였습니다.", authentication.getName());
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
