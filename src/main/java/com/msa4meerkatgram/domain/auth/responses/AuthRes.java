package com.msa4meerkatgram.domain.auth.responses;

import com.msa4meerkatgram.domain.user.entities.User;
import com.msa4meerkatgram.domain.user.responses.UserWithPostCountRes;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답")
public record AuthRes(
    UserWithPostCountRes userWithPostCountRes,
    String accessToken
) {
    public static AuthRes from(User user, String accessToken, long countPosts) {
        return new AuthRes(
                UserWithPostCountRes.from(user, countPosts),
                accessToken
        );
    }
}
