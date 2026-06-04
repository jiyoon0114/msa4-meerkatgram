package com.msa4meerkatgram.domain.file.responses;

import lombok.Builder;

@Builder
public record FileRes(
    // Uri
    String fileUri
) {}
