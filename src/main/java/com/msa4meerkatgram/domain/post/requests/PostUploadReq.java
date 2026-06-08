package com.msa4meerkatgram.domain.post.requests;

import jakarta.validation.constraints.NotBlank;

public record PostUploadReq(
    @NotBlank
    String content,

    @NotBlank
    String image
) {}
