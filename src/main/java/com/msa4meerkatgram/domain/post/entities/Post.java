package com.msa4meerkatgram.domain.post.entities;

import com.msa4meerkatgram.domain.user.entities.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity // 해당 클래스는 DB에서 관리할 JPA 엔티티임을 선언
// 이 엔티티에서 특정 이벤트가 발생하면 AuditingEntityListener에게 알려라
// 그 엔티티에 이벤트가 발생할떄 AuditingEntityListener가 createdAt, updatedAt을 자동으로 넣어주고 DB 저장함
@EntityListeners(AuditingEntityListener.class)
@Table(name = "posts") // 테이블명 맵핑
@SQLDelete(sql = "UPDATE posts SET deleted_at = NOW() WHERE id = ?") // delete 처리를 하고 싶을때 이 쿼리를 실행해라
@SQLRestriction("deleted_at IS NULL") // 엔티티 조회 시 항상 특정 조건을 추가하도록 지정
@Getter
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @Column(name = "content", nullable = false, length = 200)
    private String content;

    @Column(name = "image", nullable = false, length = 100)
    private String image;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    // 현재 작성하는 entity 기준을 앞으로 (Post가 n이니 Many)
    // 이 연관관계에서는 연관관계를 쓰겠다
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            insertable = true,
            updatable = false, // update할때 user 객체에 어떤 값을 넣더라도, update문에 user_id 칼럼을 포함하지 않겠다.
            nullable = false,
            // 물리적 외래키 제약조건 만들진 않음
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private User user;
}
