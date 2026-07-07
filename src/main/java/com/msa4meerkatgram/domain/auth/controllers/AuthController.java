package com.msa4meerkatgram.domain.auth.controllers;


import com.msa4meerkatgram.domain.auth.requsts.LoginReq;
import com.msa4meerkatgram.domain.auth.requsts.RegistrationReq;
import com.msa4meerkatgram.domain.auth.responses.AuthRes;
import com.msa4meerkatgram.domain.auth.services.AuthService;
import com.msa4meerkatgram.global.responses.GlobalRes;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<GlobalRes<AuthRes>> login(
            @Valid @RequestBody LoginReq loginReq
            // Response할때 필요한 정보를 저장해 주는 객체
            , HttpServletResponse response
        ) {
        return ResponseEntity.status(200).body(
                GlobalRes.<AuthRes>builder()
                        .code("00")
                        .messsage("로그인 완료")
                        .data(authService.login(response, loginReq))
                        .build()
        );
    }

    @PostMapping("/reissue-token")
    public ResponseEntity<GlobalRes<AuthRes>> reissue(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return ResponseEntity.status(200).body(
                GlobalRes.<AuthRes>builder()
                        .code("00")
                        .messsage("토큰 재발급 완료")
                        .data(authService.reissue(request,response))
                        .build()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<GlobalRes<String>> logout(
        HttpServletResponse response,
        // Spring Security에 등록된 현재 인증 사용자의 principal을 가져옴
        @AuthenticationPrincipal Claims claims
        ) {
        authService.logout(response, Long.parseLong(claims.getSubject()));

        return ResponseEntity.status(200).body(
            GlobalRes.<String>builder()
                    .code("00")
                    .messsage("로그아웃 완료")
                    .build()
        );
    }

    @PostMapping("/registration")
    public ResponseEntity<GlobalRes<String>> registration(
        @Valid @RequestBody RegistrationReq registrationReq
        ) {
        authService.registraion(registrationReq);

        return ResponseEntity.status(200).body(
                GlobalRes.<String>builder()
                        .code("00")
                        .messsage("회원가입 완료")
                        .build()
        );
    }
}
