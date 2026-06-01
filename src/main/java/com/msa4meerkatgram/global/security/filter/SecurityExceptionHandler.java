package com.msa4meerkatgram.global.security.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class SecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
    private final HandlerExceptionResolver handlerExceptionResolver;

    public SecurityExceptionHandler(@Qualifier("handlerExceptionResolver")HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    // 401(미인증) 관련 -> 로그인 (JWT없이) 요청
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {

        handlerExceptionResolver.resolveException(request, response, null, authException);
    }

    // 403(권한) 관련 -> 로그인은 했는데 권한이 없음
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        // Security가 우리가 적은 spring MVC 전에 실행됨
        handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);
    }
}
