package com.ota_service.ota_service.common;

import java.time.LocalDateTime;
import org.slf4j.MDC;

public record ApiResponse<T>(
        Boolean success,
        Integer code,
        String message,
        T data
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                true,
                200,
                null,
                data
        );
    }

    public static <T> ApiResponse<T> failure(Integer code, String message, T data) {
        return new ApiResponse<>(
                false,
                code,
                message,
                data
        );
    }
}
