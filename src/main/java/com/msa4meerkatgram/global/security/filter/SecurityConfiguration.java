package com.msa4meerkatgram.global.security.filter;

import com.msa4meerkatgram.global.config.CorsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// 프로젝트의 환경 설정 클래스 -> Spring이 실행될 때 클래스를 읽고 안에 있는 Bean 메서드를 실행해서 객체를 Spring Container에 등록
@Configuration
@EnableWebSecurity // Spring Security 기능을 활성화, Http 요청이 들어올 때 Security Filter Chain을 적용
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final CorsConfig corsConfig;

    // 내가 만든 클래스가 아닌 외부 라이브러리의 클래스를 스프링 가방에 넣고 싶을 때 특정 메서드 위에 붙임
    // 암호화 처리를 도와주는 객체를 반환하는 메서드
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 허락된 도메인(Origin)과 메서드인가?를 검사 -> 통과 시 다음으로 이동
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // CorsConfiguration 객체 생성: CORS 규칙을 담는 Spring 객체
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 프론트엔드 도메인 설정
        configuration.setAllowedOrigins(corsConfig.allowedOrigins());

        // 허용할 HTTP Method 지정
        configuration.setAllowedMethods(List.of(
                HttpMethod.GET.name()
                , HttpMethod.POST.name()
                , HttpMethod.PUT.name()
                , HttpMethod.PATCH.name()
                , HttpMethod.DELETE.name()
                , HttpMethod.OPTIONS.name() // preflight 요청 허용 -> 이해 제대로 안됨
        ));

        // 허용할 헤더 지정
        configuration.setAllowedHeaders(List.of(
                HttpHeaders.AUTHORIZATION // JWT 담는 헤더
                ,HttpHeaders.CONTENT_TYPE // JSON인지 mutipart인지 알려주는 헤더
                ,HttpHeaders.ACCEPT // 어떤 응답 타입을 받을지 알려주는 헤더
        ));

        // CORS 상황에서도 자격증명(Cookie, 인증 헤더 정보 등등) 포함 여부 설정
        configuration.setAllowCredentials(true);

        // 브라우저가 preflight 요청 결과를 캐시할 시간(초 단위) 설정
        configuration.setMaxAge(corsConfig.maxAge());

        // 모든 API 경로에 위 설정을 적용
        // UrlBasedCorsConfigurationSource 객체 : 어떤 URL 경로에 어떤 CORS 설정 적용할지 관리
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 백엔드 서버의 모든 경로에 CORS 설정 적용
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // SecurityUrlRegistry를 참조하여 권한 검사, 이 유저가 이 URL get or patch에 접근이 가능한가?
    // if success -> 우리가 만든 @controller
    // else if fail -> SecurityExceptionHandler (commence or handle) -> HandlerExceptionResolver
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http, // Spring security 설정을 체인 방식으로 작성하게 해주는 객체
            SecurityExceptionHandler securityExceptionHandler,
            TokenAuthenticationFilter tokenAuthenticationFilter
    ) throws Exception {
        return http
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 설정 비활성
                .httpBasic(AbstractHttpConfigurer::disable) // 브라우저 기본 인증창 비활성화.
                .formLogin(AbstractHttpConfigurer::disable) //Spring Security 기본 로그인 페이지 비활성화.
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화.
                // 아까 만든 CORS 설정을 Security Filter Chain에 적용
                .cors(cors -> cors.configurationSource(this.corsConfigurationSource())) // Cors 설정 추가
                // TokenAuthenticationFilter를 Spring Security 필터 체인에 등록함
                // UsernamePasswordAuthenticationFilter보다 먼저 TokenAuthenticationFilter를 실행해라.
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // 필터 등록
                .authorizeHttpRequests(req ->
                    // 리퀘스트에 대한 권한 설정
                    req.requestMatchers(HttpMethod.GET, SecurityUrlRegistry.AUTH_REQUIRED_GET_URLS).authenticated()
                        .requestMatchers(HttpMethod.DELETE, SecurityUrlRegistry.AUTH_REQUIRED_DELETE_URLS).authenticated()
                        .requestMatchers(HttpMethod.PATCH, SecurityUrlRegistry.AUTH_REQUIRED_PATCH_URLS).authenticated()
                        .requestMatchers(HttpMethod.POST, SecurityUrlRegistry.AUTH_REQUIRED_POST_URLS).authenticated()
                        .requestMatchers(HttpMethod.PUT, SecurityUrlRegistry.AUTH_REQUIRED_PUT_URLS).authenticated()
                        .anyRequest().permitAll() // 그 외는 인증 불필요
                )
                .exceptionHandling(e ->
                    e.authenticationEntryPoint(securityExceptionHandler)
                            .accessDeniedHandler(securityExceptionHandler)
                )
                .build();
    }
}
