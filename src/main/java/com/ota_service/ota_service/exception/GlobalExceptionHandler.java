package com.ota_service.ota_service.exception;

import com.ota_service.ota_service.common.ApiResponse;
import jakarta.persistence.LockTimeoutException;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException exception) {
        ExceptionType exceptionType = exception.getExceptionType();
        return ResponseEntity.status(exceptionType.status())
                .body(ApiResponse.failure(exceptionType.code(), exception.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ExceptionType exceptionType = ExceptionType.INVALID_INPUT;
        return ResponseEntity.status(exceptionType.status())
                .body(ApiResponse.failure(exceptionType.code(), exceptionType.message(), errors));
    }

    @ExceptionHandler({
            PessimisticLockingFailureException.class,
            CannotAcquireLockException.class,
            LockTimeoutException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleLockException(Exception exception) {
        log.warn("Lock acquisition failed", exception);
        ExceptionType exceptionType = ExceptionType.RESOURCE_BUSY;
        return ResponseEntity.status(exceptionType.status())
                .body(ApiResponse.failure(exceptionType.code(), exceptionType.message(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
        log.error("Unhandled exception", exception);
        ExceptionType exceptionType = ExceptionType.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(exceptionType.status())
                .body(ApiResponse.failure(exceptionType.code(), exceptionType.message(), null));
    }
}
