package com.msa4meerkatgram.domain.post.responses;

import lombok.Builder;

@Builder
public record PostRes(
    long id,
    long userId,
    String content,
    String image,
    String createdAt,
    String updatedAt,
    String deletedAt
) { }
