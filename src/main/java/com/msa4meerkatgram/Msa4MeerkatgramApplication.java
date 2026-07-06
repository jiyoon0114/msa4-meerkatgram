package com.msa4meerkatgram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ConfigurationPropertiesScan
// 어플리케이션 전체에 JPA Auditing 기능을 사용할테니 준비해라. 선언
// created_at, updated_at 자동으로 채워줌
@EnableJpaAuditing
public class Msa4MeerkatgramApplication {
    public static void main(String[] args) {
        SpringApplication.run(Msa4MeerkatgramApplication.class, args);
    }
}
