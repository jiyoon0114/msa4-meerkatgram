package com.msa4meerkatgram.global.security.constant;

import lombok.Getter;

@Getter
public enum ProviderPolicy {
//    private ProviderPolicy NONE = ProviderPolicy("NONE");을 단축으로 세팅
    NONE("NONE"),
    KAKAO("KAKAO"),
    GOOGLE("GOOGLE");

    private final String provider;

    ProviderPolicy(String provider) {
        this.provider = provider;
    }
}
