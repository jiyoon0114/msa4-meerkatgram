package com.msa4meerkatgram.global.errors.custom;

// 커스텀 에러는 보통 RuntimeException을 상속 받음
public class NotRegisteredException extends RuntimeException {
    public NotRegisteredException(String message) {
        super(message);
    }
}
