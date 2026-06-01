package com.msa4meerkatgram.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "cors")
public record CorsConfig(
    // 요청이 와도 괜찮은 허용하는 주소들
    List<String> allowedOrigins,
    Long maxAge
) {}
