package com.msa4meerkatgram.global.security.filter;

import com.msa4meerkatgram.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SecurityAuthenticationProvider {
    private final JwtProvider jwtProvider;

    // JWT를 Spring Security가 이해하는 Authentication 객체로 반환
    // Authentication: Spring Security의 현재 로그인한 사용자
    // 스프링 시큐리티에서 사용자의 인증정보를 담는 객체를 생성 -> payload를 받아 유저 객체 생성
    // 각 아규먼트는 인증된 사용자 객체(Claims), 비밀번호 저장 여부, 사용자 권한 목록
    public Authentication authentication(String token) {
        return new UsernamePasswordAuthenticationToken(
                jwtProvider.extractClaims(token), // 1. Principal (사용자 정보)
                null,                                   // 2. Credentials (비밀번호, 통상 null)
                List.of()                               // 3. Authorities (권한 목록)
        );
    }
}
