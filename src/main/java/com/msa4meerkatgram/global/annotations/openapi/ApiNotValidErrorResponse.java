package com.msa4meerkatgram.global.annotations.openapi;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME) // runtime때 실행되는 에노테이션
@ApiResponse(
        responseCode = "400",
        description = "유효성 검사 실패",
        content = @Content(
                mediaType = "application/json",
                examples = {
                        @ExampleObject(
                                name = "유효성 검사 실패",
                                value = "{\"code\":\"E21\",\"message\":\"요청 파라미터에 이상이 있습니다.\",\"data\":\"Bad Request\"}"
                        )
                }
        )
)
public @interface ApiNotValidErrorResponse {
}
