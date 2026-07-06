package com.msa4meerkatgram.domain.user.entities;

import com.msa4meerkatgram.global.security.constant.ProviderPolicy;
import com.msa4meerkatgram.global.security.constant.RolePolicy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Types;
import java.time.LocalDateTime;

@Entity // 해당 클래스는 DB에서 관리할 JPA 엔티티임을 선언
// 이 엔티티에서 특정 이벤트가 발생하면 AuditingEntityListener에게 알려라
// 그 엔티티에 이벤트가 발생할떄 AuditingEntityListener가 createdAt, updatedAt을 자동으로 넣어주고 DB 저장함
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users") // 테이블명 맵핑
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?") // delete 처리를 하고 싶을때 이 쿼리를 실행해라
@SQLRestriction("deleted_at IS NULL") // 엔티티 조회 시 항상 특정 조건을 추가하도록 지정
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // pk 자동 생성 전략 설정, GenerationType.IDENTITY가 auto increment
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED") // DB에서 쓰는 칼럼명, DB 데이터타입 매핑
    private long id;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "nickname", nullable = false, length = 20)
    private String nickName;

    @Column(name = "provider", nullable = false, length = 10)
    @Enumerated(value = EnumType.STRING) // Enum을 어떤 데이터형식으로 저장할 건지 설정
    @JdbcTypeCode(Types.VARCHAR) // Hibernate에게 DB 컬럼 타입을 명확하게 VARCHAR로 처리하라고 알려주는 설정
    private ProviderPolicy provider = ProviderPolicy.NONE;

    @Column(name = "role", nullable = false, length = 20)
    @Enumerated(value = EnumType.STRING)
    @JdbcTypeCode(Types.VARCHAR)
    private RolePolicy role = RolePolicy.NORMAL;

    @Column(name = "profile", nullable = false, length = 100)
    private String profile;

    @Column(name = "refresh_token", nullable = true, length = 255)
    private String refreshToken;

    @CreatedDate // 생성 시 자동으로 현재 시간 입력
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // 수정시 자동으로 현재 시간 입력
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;
}