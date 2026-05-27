package com.msa4meerkatgram.domain.auth.controllers;

import com.msa4meerkatgram.domain.auth.requsts.LoginReq;
import com.msa4meerkatgram.domain.auth.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> login(
            @Valid @RequestBody LoginReq loginReq
            // Response할때 필요한 정보를 저장해 주는 객체
            , HttpServletRequest request
            ) {
        authService.login(loginReq);
        return ResponseEntity.status(200).body("test");
    }
}
