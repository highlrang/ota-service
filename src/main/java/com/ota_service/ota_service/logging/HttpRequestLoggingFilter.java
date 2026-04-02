package com.ota_service.ota_service.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public class HttpRequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestLoggingFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final int MAX_LOG_BODY_LENGTH = 5_000;
    private static final Pattern SENSITIVE_JSON_FIELD_PATTERN =
            Pattern.compile("\"(?i)(password|passwd|token|accessToken|refreshToken|secret)\"\\s*:\\s*\".*?\"");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestId = resolveRequestId(request);
        long startedAt = System.currentTimeMillis();
        ContentCachingRequestWrapper requestWrapper = wrapRequest(request);
        ContentCachingResponseWrapper responseWrapper = wrapResponse(response);

        MDC.put("requestId", requestId);
        response.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startedAt;
            String requestBody = extractRequestBody(requestWrapper);
            String responseBody = extractResponseBody(responseWrapper);
            log.info(
                    "HTTP {} {} status={} durationMs={} requestBody={} responseBody={}",
                    requestWrapper.getMethod(),
                    requestWrapper.getRequestURI(),
                    responseWrapper.getStatus(),
                    duration,
                    requestBody,
                    responseBody
            );
            responseWrapper.copyBodyToResponse();
            MDC.clear();
        }
    }

    private ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper wrappedRequest) {
            return wrappedRequest;
        }
        return new ContentCachingRequestWrapper(request);
    }

    private ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper wrappedResponse) {
            return wrappedResponse;
        }
        return new ContentCachingResponseWrapper(response);
    }

    private String extractRequestBody(ContentCachingRequestWrapper requestWrapper) {
        if (!isLoggableContentType(requestWrapper.getContentType())) {
            return "[omitted]";
        }
        byte[] body = requestWrapper.getContentAsByteArray();
        if (body.length == 0) {
            return "[empty]";
        }
        return sanitizeAndTrim(toBodyString(body, requestWrapper.getCharacterEncoding()));
    }

    private String extractResponseBody(ContentCachingResponseWrapper responseWrapper) {
        if (!isLoggableContentType(responseWrapper.getContentType())) {
            return "[omitted]";
        }
        byte[] body = responseWrapper.getContentAsByteArray();
        if (body.length == 0) {
            return "[empty]";
        }
        return sanitizeAndTrim(toBodyString(body, responseWrapper.getCharacterEncoding()));
    }

    private boolean isLoggableContentType(String contentType) {
        if (!StringUtils.hasText(contentType)) {
            return false;
        }
        String normalized = contentType.toLowerCase();
        return normalized.contains("application/json")
                || normalized.contains("application/xml")
                || normalized.contains("application/x-www-form-urlencoded")
                || normalized.contains("text/");
    }

    private String toBodyString(byte[] body, String characterEncoding) {
        Charset charset = StandardCharsets.UTF_8;
        if (StringUtils.hasText(characterEncoding)) {
            try {
                charset = Charset.forName(characterEncoding);
            } catch (Exception ignored) {
                charset = StandardCharsets.UTF_8;
            }
        }
        return new String(body, charset);
    }

    private String sanitizeAndTrim(String body) {
        String sanitized = SENSITIVE_JSON_FIELD_PATTERN.matcher(body).replaceAll("\"$1\":\"***\"");
        if (sanitized.length() > MAX_LOG_BODY_LENGTH) {
            return sanitized.substring(0, MAX_LOG_BODY_LENGTH) + "...(truncated)";
        }
        return sanitized;
    }

    private String resolveRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (StringUtils.hasText(requestId)) {
            return requestId;
        }
        return UUID.randomUUID().toString();
    }
}
