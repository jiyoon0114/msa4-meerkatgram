package com.msa4meerkatgram.global.errors;

import com.msa4meerkatgram.global.responses.GlobalRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.List;

// 커스텀한 Exception을 위해 만든 객체
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 클라이언트가 보낸 하나의 타입 파라미터가 미스 매치됐을때 실행됨
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<GlobalRes<String>> methodArgumentTypeMismatchHandle(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.status(400).body(
            GlobalRes.<String>builder()
                .code("E21")
                .messsage("요청 파라미터에 이상이 있습니다")
                .data(String.format("%s : 필드를 확인해 주세요.", e.getName()))
                .build()
        );
    }

    // 클라이언트가 보낸 복수의 파라미터가 미스매차됨
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalRes<List<String>>> methodArgumentNotValidHandle(MethodArgumentNotValidException e) {
        return ResponseEntity.status(400).body(
            GlobalRes.<List<String>>builder()
                .code("E21")
                .messsage("요청 파라미터에 이상이 있습니다")
                .data(
                    /*
                    BindingResult -> Validation 전체 결과를 담는 객체
                     ├─ errors(List<ObjectError>)
                     │    ├─ title 에러
                     │    ├─ content 에러
                     │    └─ age 에러
                     │
                     ├─ objectName
                     ├─ targetObject
                     └─ 기타 바인딩 정보
                     */
                    // getBindingResult() return BindingResult: Validation 결과 + 데이터 바인딩 결과 -> 어떤 필스가 실패했는지 기록해둔 객체
                    e.getBindingResult()
                        // getAllErrors() return List<ObjectError>: BindingResult 안에 List<ObjectError>를 꺼냄
                        .getAllErrors()
                        .stream()
                        .map(item -> String.format("%s : 잘못된 값입니다", e.getFieldError()))
                        .toList()
                )
                .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalRes<String>> otherHandle(Exception e) {
        log.error(String.format("시스템 에러: %s\n%s", e.getMessage(), Arrays.toString(e.getStackTrace())));
        return ResponseEntity.status(500).body(
            GlobalRes.<String>builder()
                .code("E99")
                .messsage("시스템 에러")
                .data("현재 서비스 이용이 불가합니다 잠시후 다시 시도해주세요")
                .build()
        );
    }
}
