package twodorian.todo._core.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import twodorian.todo._core.error.ApplicationException;
import twodorian.todo._core.error.ErrorCode;
import twodorian.todo.member.command.application.dto.MemberAuthResponseDTO;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JWTTokenProvider {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final String CLAIM_TYPE = "type";

    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";
    // Access token 의 시간 : 1일
    private static final long ACCESS_TOKEN_LIFETIME = 24 * 60 * 60 * 1000L;
    // Refresh token 의 시간 : 3일
    private static final long REFRESH_TOKEN_LIFETIME = 3 * 24 * 60 * 60 * 1000L;

    // jwt 토큰 암호화를 위한 키
    private final Key secretKey;

    public JWTTokenProvider(@Value("${JWT_SECRET}") String secretKey) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public MemberAuthResponseDTO.authTokenDTO generateToken(Authentication authentication) {
        return generateToken(authentication.getName(), authentication.getAuthorities());
    }

    public MemberAuthResponseDTO.authTokenDTO generateToken(String name, Collection<? extends GrantedAuthority> grantedAuthorities) {
        // 권한 확인
        String authorities = grantedAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 현재 시간
        Date now = new Date();

        // Access 토큰 제작
        String accessToken = Jwts.builder()
                // 아이디 주입
                .setSubject(name)
                // 권한 주입
                .claim(AUTHORITIES_KEY, authorities)
                .claim(CLAIM_TYPE, TYPE_ACCESS)
                // 토큰 발행 시간 정보
                .setIssuedAt(now)
                // 만료시간 주입
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_LIFETIME))
                // 암호화
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        // Refresh 토큰 제작
        String refreshToken = Jwts.builder()
                .claim(CLAIM_TYPE, TYPE_REFRESH)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_LIFETIME))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return new MemberAuthResponseDTO.authTokenDTO(BEARER_TYPE, accessToken, ACCESS_TOKEN_LIFETIME, refreshToken, REFRESH_TOKEN_LIFETIME);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            throw new ApplicationException(ErrorCode.INVALID_TOKEN);
        }
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT Token 입니다.", e);
            return e.getClaims();
        }
    }

    // JWT 토큰 복호화 -> 토큰 정보 확인
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        Collection<? extends GrantedAuthority> authorities =
                Optional.ofNullable(claims.get(AUTHORITIES_KEY))
                        .map(Object::toString)
                        .map(auth -> Arrays.stream(auth.split(","))
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList()))
                        .orElseThrow(() -> new ApplicationException(ErrorCode.FORBIDDEN));

        // UserDetails 객체를 Principal, Crendential, Authorities와 함께 생성
        // 이후 Authentication 객체를 반환
        // 이때 유저는 Spring boot 자체 User class
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean isRefreshToken(String token) {
        String type = (String) Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().get(CLAIM_TYPE);
        return type.equals(TYPE_REFRESH);
    }

    public String resolveToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER))
                .filter(StringUtils::hasText)
                .filter(token -> token.startsWith(BEARER_TYPE))
                .map(token -> token.substring(7))
                .orElse(null);
    }
}
