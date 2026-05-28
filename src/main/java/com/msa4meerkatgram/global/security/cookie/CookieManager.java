package com.msa4meerkatgram.global.security.cookie;


import com.msa4meerkatgram.global.security.jwt.JwtConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CookieManager {

    private final JwtConfig jwtConfig;

    // Request에서 특정 쿠키를 획득하는 메서드
    // Cookie가 아니라 Optional을 반환하는 이유: 이 메서드가 null을 반환할 수 있다는걸 우리 팀원 모두가 알고 있어야함
    // -> Optional을 이용하면 null 처리를 안 하면 알려줌
    // HttpServletRequest객체: 클라이언트 HTTP 요청 전체 객체 -> 헤더 + 쿠키 + 요청 URL + 바디 등등
    public Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        // request의 cookie의 모든 요소를 전부 가져옴 -> Cookie 배열로 옴
        if(request.getCookies() == null) {
            // Optianal에 있는 데이터가 비어있는걸 리턴함
            return Optional.empty();
        }
        // Cookie 배열 중에서 특정 이름(name)의 Cookie가 필요함
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst();
    }

    // 쿠키 생성 메소드
    // HttpServletResponse: 클라이언트에게 Response할 HTTP 응답 객체
    public void setCookie(HttpServletResponse response, String name, String value, int maxAge, String path) { {
        Cookie cookie = new Cookie(name, value); // 해당 이름과 값으로 쿠키 인스턴스 생성
        // 쿠키가 유효한 경로를 설정 -> 특정 경로(예: /api)를 넣으면 브라우저가 그 경로로 요청할 때만 이 쿠키를 서버로 보냅니다.
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);// 쿠키 유효 수명 지정 -> 지정된 시간이 지나면 브라우저가 쿠키를 폐기
        cookie.setHttpOnly(true); // HttpOnly 설정 XSS 공격 방지 설정 -> 자바 스크립(document.cookie)을 통해 쿠키를 접근할 수 없게 됨
        // 브라우저가 HTTPS Request를 보낼때만 쿠키 보낼거야? -> true or false
        cookie.setSecure(jwtConfig.secure());

        // Response HTTP응답헤더에 쿠키 넣기
        response.addCookie(cookie);
        }
    }
}
