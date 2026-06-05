package com.msa4meerkatgram.global.errors;

import com.msa4meerkatgram.global.errors.custom.*;
import com.msa4meerkatgram.global.responses.GlobalRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

// 커스텀한 Exception을 위해 만든 객체
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 클라이언트가 보낸 로그인이 양식에 맞지 않음
    @ExceptionHandler(NotRegisteredException.class)
    public ResponseEntity<GlobalRes<String>> notRegisteredException(NotRegisteredException e) {
        return ResponseEntity.status(401).body(
                GlobalRes.<String>builder()
                        .code("E01")
                        .messsage("로그인 에러")
                        .data(e.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<GlobalRes<String>> authenticationHandle(AuthenticationException e) {
        return ResponseEntity.status(401).body(
                GlobalRes.<String>builder()
                        .code("E02")
                        .messsage("UNAUTHENTICATED_ERROR")
                        .data("로그인이 필요한 서비스입니다.")
                        .build()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GlobalRes<String>> accessDeniedHandle(AccessDeniedException e) {
        return ResponseEntity.status(403).body(
                GlobalRes.<String>builder()
                        .code("E03")
                        .messsage("UNAUTHORIZED_ERROR")
                        .data("권한이 부족합니다")
                        .build()
        );
    }

    // 클라이언트 토큰 만료
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<GlobalRes<String>> invalidTokenHandle(InvalidTokenException e) {
        return ResponseEntity.status(401).body(
                GlobalRes.<String>builder()
                        .code("E04")
                        .messsage("토큰 이상")
                        .data(e.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(DeletedRecordException.class)
    public ResponseEntity<GlobalRes<String>> deletedRecordHandler(DeletedRecordException e) {
        return ResponseEntity.status(404).body(
                GlobalRes.<String>builder()
                        .code("E10")
                        .messsage("DELETED_RECORD_ERROR")
                        .data(e.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(DuplicatedRecordException.class)
    public ResponseEntity<GlobalRes<String>> duplicatedRecordHandle(DuplicatedRecordException e) {
        return ResponseEntity.status(404).body(
                GlobalRes.<String>builder()
                        .code("E11")
                        .messsage("DUPLICATED_RECORD_ERROR")
                        .data(e.getMessage())
                        .build()
        );
    }

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
    public ResponseEntity<GlobalRes<Map<String, String>>> methodArgumentNotValidHandle(MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField, // 필드명
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "유효하지 않은 값입니다.",
                        (existing, replacement) -> existing // 중복 필드가 있을 경우 기존 값 유지
                ));

        return ResponseEntity.status(400).body(
                GlobalRes.<Map<String, String>>builder()
                        .code("E21")
                        .messsage("요청 파라미터에 이상이 있습니다.")
                        .data(errors)
                        .build()
        );
    }

    @ExceptionHandler(FileManagedException.class)
    public ResponseEntity<GlobalRes<String>> fileManagedHandle(FileManagedException e) {
        log.error("파일 업로드 에러:", e);
        return ResponseEntity.status(500).body(
                GlobalRes.<String>builder()
                        .code("E40")
                        .messsage("파일 업로드 실패")
                        .data(e.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<GlobalRes<String>> SQLHandle(SQLException e) {
        log.error("DB 에러: ", e);
        return ResponseEntity.status(500).body(
                GlobalRes.<String>builder()
                        .code("E80")
                        .messsage("DB 에러")
                        .data("현재 서비스 이용이 불가합니다 잠시후 다시 시도해주세요")
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalRes<String>> otherHandle(Exception e) {
        log.error("시스템 에러: ", e);
        return ResponseEntity.status(500).body(
            GlobalRes.<String>builder()
                .code("E99")
                .messsage("시스템 에러")
                .data("현재 서비스 이용이 불가합니다 잠시후 다시 시도해주세요")
                .build()
        );
    }
}
