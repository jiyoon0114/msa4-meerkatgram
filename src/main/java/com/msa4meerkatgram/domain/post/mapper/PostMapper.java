package com.msa4meerkatgram.domain.post.mapper;

import com.msa4meerkatgram.domain.post.entities.Post;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {
    List<Post> getPaination(int limit, int offset);
    long getTotal();
    Post findbyPK(long id);
    long countPostByUserId(long userId);
    int upload(Post post);
}
