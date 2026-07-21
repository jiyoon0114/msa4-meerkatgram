package com.msa4meerkatgram.domain.post.repositories;

import com.msa4meerkatgram.domain.post.entities.Post;
import com.msa4meerkatgram.domain.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

// 첫번째 제네릭은 레포지토리에서 쓰는 엔테티, 두번째는 그 엔티티 PK type
public interface PostRepository extends JpaRepository<Post, Long> {
    long countByUser(User user);
}
