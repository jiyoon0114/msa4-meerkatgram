package com.msa4meerkatgram.domain.user.services;

import com.msa4meerkatgram.domain.auth.responses.AuthRes;
import com.msa4meerkatgram.domain.user.entities.User;
import com.msa4meerkatgram.domain.user.mapper.UserMapper;
import com.msa4meerkatgram.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JwtProvider jwtProvider;
    private final UserMapper userMapper;

    public AuthRes test() {
        User user = userMapper.findByPk(17);

        String newAccessToken = jwtProvider.generateAccessToken(user);
        String newRefreshToken = jwtProvider.generateFreshToken(user);
        System.out.println(newRefreshToken);
        return AuthRes.builder()
                .user(user)
                .accessToken(newAccessToken)
                .build();
    }
}
