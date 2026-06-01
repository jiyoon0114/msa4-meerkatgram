package com.msa4meerkatgram.global.security.filter;

import com.msa4meerkatgram.global.security.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException; // 1. ServletException 임포트 추가
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException; // 2. IOException 임포트 추가
import java.util.Optional;

// 요청마다 1번씩 실행되는 JWT 필터

@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final SecurityAuthenticationProvider securityAuthenticationProvider;
    private final HandlerExceptionResolver handlerExceptionResolver;

    // OncePerRequestFilter를 상속 받은 클라스 만약 인수로 이 객체가 들어온 경우 안에 있는 메서드가 자동으로 실행해준다
    // Authorization 헤더 읽기
    //Bearer 토큰 추출
    //JWT 검증
    //Authentication 객체 생성
    //SecurityContextHolder에 저장
    @Override
    protected void doFilterInternal(@NonNull  HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 헤더에서 엑세스 토큰 추출
        Optional<String> tokenOptional = jwtProvider.extractAccessToken(request);

        // 토큰이 존재할 때만 인증 로직 실행
        // 값이 비어있는지 체크하고 있으면 true return
        if(tokenOptional.isPresent()) {
            try {
                // Security 인증 정보 설정 -> Authentication을 보고 인증된 사람인지 -> 완료 후 로그인 완료 상태가 됨
                SecurityContextHolder.getContext().setAuthentication(securityAuthenticationProvider.authentication(tokenOptional.get()));
            } catch (Exception e) {
                // 예외를 핸들러 리졸버로 위임(@RestControllerAdvice가 처리하도록 함)
                handlerExceptionResolver.resolveException(request, response, null, e);
                // 예외가 발생했을때 응답 완료 후 필터 체인을 중단하기 위해 return
                return;
            }
        }

        // 다음 필터 호출 (이 메서드가 ServletException과 IOException을 던집니다)
        filterChain.doFilter(request, response);
    }
}