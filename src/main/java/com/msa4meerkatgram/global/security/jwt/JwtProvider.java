package com.msa4meerkatgram.global.security.jwt;

import com.msa4meerkatgram.domain.user.entities.User;
import com.msa4meerkatgram.global.errors.custom.InvalidTokenException;
import com.msa4meerkatgram.global.security.cookie.CookieManager;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

// class를 자동으로 자바 bean이 관리하게 해줌 -> 일일이 인스턴스화 할 필요가 없음
@Component
public class JwtProvider {
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;
    private final CookieManager cookieManager;

    public JwtProvider(JwtConfig jwtConfig, CookieManager cookieManager) {
        this.jwtConfig = jwtConfig;
        // Decoders.BASE64.decode Base64인코딩된 문자열을 디코드해서 byte배열로 return
        // return byte를 jwt 서명용 열쇠 객체로 만드는 과정
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfig.secret()));
        this.cookieManager = cookieManager;
    }

    public String generateAccessToken(User user) {
        return this.generateToken(user, jwtConfig.accessTokenExpiry());
    }


    public String generateFreshToken(User user) {
        return this.generateToken(user, jwtConfig.refreshTokenExpiry());
    }

    // time to limit = ttl
    // Token 발급하기
    private String generateToken(User user, long ttl) {
        Date now = new Date();
        return Jwts.builder()
                .header()
                .type(jwtConfig.type()) // 토큰 유형 설정
                .and()// header()호출 BuilderHeader -> payload 쓸 메서드 적어야함 -> and()로 JwtBuilder로 돌아가기
                .subject(String.valueOf(user.getId())) // 유저를 특정하는 id 셋팅에 주로 사용
                .issuer(jwtConfig.issuer()) // 토큰 발급자
                .issuedAt(now) // 발급 시간
                .expiration(new Date(now.getTime() + ttl)) // 만료시간
                .claim("role", user.getRole()) // private claim 설정
                .signWith(secretKey) // SecretKey로 JWT에 서명
                .compact(); // JWT를 최종 문자열로 만든다 -> Header.Payload.Signature 형태로 만듦
    }

    //쿠키에서 리프레시 토큰 추출
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return cookieManager.getCookie(request, jwtConfig.refreshTokenCookieName())
                .map(Cookie::getValue);
    }
    // 쿠키에서 엑세스 토큰 추출
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtConfig.headerKey());

        if(bearerToken == null || !bearerToken.startsWith(jwtConfig.scheme())) {
            return Optional.empty();
        }

        return Optional.of(bearerToken.substring(jwtConfig.scheme().length()).trim());
    }

    // 토큰 검증 및 클래임 추출
    // Claims -> JWT payload에 들어 있는 정보 묶음
    public Claims extractClaims(String token) {
        try {
            // JWT 검사기 만들기 시작
            return Jwts.parser()
                    // 토큰의 Signature를 검증할때 쓸 SecretKey를 지정하는 부분
                    .verifyWith(this.secretKey)
                    // 앞에서 설정한 조건으로 JWT Parser 완성
                    .build()
                    // token을 Header/ payload /signature 분리 + signatnature 검증, 만료 시간 검증, 형식 검증 수행
                    // 문제 없으면 JWs<Claims> 반환
                    .parseSignedClaims(token)
                    // Jwts<Claims>에서 Claims만 추출함
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("토큰이 만료됐습니다.");
        } catch (UnsupportedJwtException e) {
            throw new InvalidTokenException("서명이 위조된 토큰입니다");
        } catch (MalformedJwtException e) {
            throw new InvalidTokenException("토큰형식이 올바르지 않습니다");
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("토큰 검증에 실패했습니다");
        }
    }
}
