package com.msa4meerkatgram.global.security.constant;

import lombok.Getter;

@Getter
public enum RolePolicy {
//    private ProviderPolicy NONE = ProviderPolicy("NONE");을 단축으로 세팅
    NORMAL("NONE"),
    SUPER("KAKAO");

    private final String role;

    RolePolicy(String role) {
        this.role = role;
    }
}
