package com.ota_service.ota_service.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ExceptionType {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, 4000, "잘못된 요청입니다."),
    INVALID_AUTH(HttpStatus.UNAUTHORIZED, 4011, "이메일 또는 비밀번호가 올바르지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 4012, "인증이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, 4030, "접근 권한이 없습니다."),
    ACCOUNT_INACTIVE(HttpStatus.FORBIDDEN, 4031, "비활성화된 계정입니다."),
    NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED, 5001, "아직 구현되지 않은 기능입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5000, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final int code;
    private final String message;

    public HttpStatus status() {
        return status;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
