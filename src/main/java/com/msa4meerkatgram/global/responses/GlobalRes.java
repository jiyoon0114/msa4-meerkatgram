package com.msa4meerkatgram.global.responses;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GlobalRes<T> {
    private String code;
    private String messsage;
    private T data;
}
