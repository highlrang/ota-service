package com.ota_service.ota_service.exception;

public class ApiException extends RuntimeException {

    private final ExceptionType exceptionType;

    public ApiException(ExceptionType exceptionType) {
        super(exceptionType.message());
        this.exceptionType = exceptionType;
    }

    public ApiException(ExceptionType exceptionType, String message) {
        super(message);
        this.exceptionType = exceptionType;
    }

    public ExceptionType getExceptionType() {
        return exceptionType;
    }
}
