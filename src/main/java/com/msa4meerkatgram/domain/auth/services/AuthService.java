package com.msa4meerkatgram.domain.auth.services;


import com.msa4meerkatgram.domain.auth.repositories.AuthRepository;
import com.msa4meerkatgram.domain.auth.requsts.LoginReq;
import com.msa4meerkatgram.domain.auth.requsts.RegistrationReq;
import com.msa4meerkatgram.domain.auth.responses.AuthRes;
import com.msa4meerkatgram.domain.post.repositories.PostRepository;
import com.msa4meerkatgram.domain.user.entities.User;
import com.msa4meerkatgram.global.errors.custom.InvalidTokenException;
import com.msa4meerkatgram.global.errors.custom.NotRegisteredException;
import com.msa4meerkatgram.global.security.cookie.CookieManager;
import com.msa4meerkatgram.global.security.jwt.JwtConfig;
import com.msa4meerkatgram.global.security.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final CookieManager cookieManager;
    private final JwtConfig jwtConfig;
    private final PasswordEncoder passwordEncoder;
    private final AuthRepository authRepository;
    private final PostRepository postRepository;

    /**
     * 액세스토큰 및 리프래시토큰 생성 후, 리프래시 토큰 DB와 Cookie에 저장, AuthRes로 반환
     * @param response HttpServletResponse
     * @param loginReq LoginReq
     * @return AuthRes
     */

    public AuthRes login(HttpServletResponse response, LoginReq loginReq) {
        // 유저정보 획득 + 유저 가입 여부 확인
        User user = authRepository.findByEmail(loginReq.email())
                .orElseThrow(() -> new NotRegisteredException("아이디와 비밀번호를 확인해주세요"));

        // 비밀번호 체크
        if(!passwordEncoder.matches(loginReq.password(), user.getPassword())) {
            throw new NotRegisteredException("아이디와 비밀번호를 확인해주세요");
        }

        return this.generateAuthentication(response, user);
    }

    public AuthRes reissue(HttpServletRequest request, HttpServletResponse response) {
        // 리프래시 토큰 획득
//        Optional<String> refreshTokenOptional = jwtProvider.extractRefreshToken(request);
//        if(refreshTokenOptional.isEmpty()) {
//            throw new InvalidTokenException("토큰이 없습니다");
//        }
//        String extractRefreshToken = refreshTokenOptional.get();

        String extractRefreshToken = jwtProvider.extractRefreshToken(request).orElseThrow(() -> new InvalidTokenException("토큰이 없습니다"));
        long id = Long.parseLong(jwtProvider.extractClaims(extractRefreshToken).getSubject());

        // 유저 획득
        User user = authRepository.findById(id).orElseThrow(() -> new InvalidTokenException("유효하지 않은 회원의 토큰입니다"));

        if(user.getRefreshToken() == null) {
            throw new InvalidTokenException("유효하지 않은 회원의 토큰입니다");
        }

        // DB의 refresh토큰과 request에서 추출한 refresh 토큰이 같은지 검증
        if(!user.getRefreshToken().equals(extractRefreshToken)) {
            throw new InvalidTokenException("토큰이 일치하지 않습니다");
        }

        return this.generateAuthentication(response, user);
    }

    // 로그인 or 토큰 재발급 성공 시 공통으로 사용하는 private 메서드
    private AuthRes generateAuthentication(HttpServletResponse response, User user) {
        // 작성 게시글 수 획득
        long countPosts = postRepository.countByUser(user);

        // 토큰 생성
        String newAccessToken = jwtProvider.generateAccessToken(user);
        String newRefreshToken = jwtProvider.generateFreshToken(user);

        // 리프래시 토큰을 DB에 저장
        user.setRefreshToken(newRefreshToken);
        // 원본과 달라진 부분만 수정(update)해서 저장함, upsert(업서트)
        authRepository.save(user);

        // 리프래시 토큰을 Response의 Cookie에 저장함
        cookieManager.setCookie(
            response
            , jwtConfig.refreshTokenCookieName()
            , newRefreshToken
            , jwtConfig.refreshTokenCookieExpiry()
            , jwtConfig.reissUri()
        );

        // 리턴
        return AuthRes.from(user, newAccessToken, countPosts);
    }

    // Exception이 발생했을때 rollback이 발생
    @Transactional(rollbackFor = Exception.class)
    public void logout(HttpServletResponse response, long id) {
        // 유저 정보 획득
        User user = authRepository.findById(id).orElseThrow(() -> new InvalidTokenException("유효하지 않은 회원의 토큰입니다."));

        // DB에 저장한 리프래시 토큰 파기
        user.setRefreshToken(null);
        authRepository.save(user);

        // Cookie에 저장한 리프래시 토큰 파기 -> maxAge = 0 -> 받자마자 쿠키 없어짐
        cookieManager.setCookie(response, jwtConfig.refreshTokenCookieName(), null, 0, jwtConfig.reissUri());
    }

    @Transactional(rollbackFor = Exception.class)
    public void registraion(RegistrationReq registrationReq) {
        // 유저 가입여부 확인 (exist 쿼리를 사용하여 대용량 환경에서 효율이 증가)
        if(authRepository.existsByEmail(registrationReq.email())) {
            throw new NotRegisteredException("이미 가입된 회원입니다");
        }

        User newUser = new User();

        newUser.setEmail(registrationReq.email());
        newUser.setPassword(passwordEncoder.encode(registrationReq.password()));
        newUser.setNickName(registrationReq.nickname());
        newUser.setProfile(registrationReq.profile());

        authRepository.save(newUser);
    }
}
