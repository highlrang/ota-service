package com.ota_service.ota_service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ota_service.ota_service.common.ApiResponse;
import com.ota_service.ota_service.exception.ExceptionType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Set<String> PUBLIC_URIS = Set.of(
            "/swagger-ui.html",
            "/api/customer/auth/login",
            "/api/extranet/auth/login"
    );
    private static final Set<String> PUBLIC_CUSTOMER_GET_PREFIXES = Set.of(
            "/api/customer/accommodations"
    );
    private static final Set<String> OPTIONAL_AUTH_CUSTOMER_POST_URIS = Set.of(
            "/api/customer/reservations"
    );

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            if (isPublicRequest(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (isOptionalAuthenticatedRequest(request)) {
                handleOptionalAuthenticatedRequest(request, response, filterChain);
                return;
            }

            AccountType requiredAccountType = getRequiredAccountType(request);
            if (requiredAccountType == null) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = extractBearerToken(request);
            if (token == null) {
                writeErrorResponse(response, ExceptionType.UNAUTHORIZED);
                return;
            }

            if (!jwtTokenProvider.isValidToken(token)) {
                writeErrorResponse(response, ExceptionType.UNAUTHORIZED);
                return;
            }

            AuthenticatedAccount account = jwtTokenProvider.getAuthenticatedAccount(token);
            if (account.accountType() != requiredAccountType) {
                writeErrorResponse(response, ExceptionType.ACCESS_DENIED);
                return;
            }

            setAuthentication(account, request);
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
            MDC.remove("userId");
        }
    }

    private boolean isPublicRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return PUBLIC_URIS.contains(requestUri)
                || isPublicCustomerGetRequest(request)
                || requestUri.startsWith("/swagger-ui/")
                || requestUri.startsWith("/v3/api-docs/");
    }

    private boolean isOptionalAuthenticatedRequest(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && OPTIONAL_AUTH_CUSTOMER_POST_URIS.contains(request.getRequestURI());
    }

    private boolean isPublicCustomerGetRequest(HttpServletRequest request) {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return false;
        }

        String requestUri = request.getRequestURI();
        return PUBLIC_CUSTOMER_GET_PREFIXES.stream().anyMatch(requestUri::startsWith);
    }

    private void handleOptionalAuthenticatedRequest(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = extractBearerToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtTokenProvider.isValidToken(token)) {
            writeErrorResponse(response, ExceptionType.UNAUTHORIZED);
            return;
        }

        AuthenticatedAccount account = jwtTokenProvider.getAuthenticatedAccount(token);
        if (account.accountType() != AccountType.CUSTOMER) {
            writeErrorResponse(response, ExceptionType.ACCESS_DENIED);
            return;
        }

        setAuthentication(account, request);
        filterChain.doFilter(request, response);
    }

    private String extractBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith("Bearer ")) {
            return null;
        }
        return bearerToken.substring(7);
    }

    private void setAuthentication(AuthenticatedAccount account, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        account,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + account.accountType().name()))
                );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        MDC.put("userId", String.valueOf(account.accountId()));
    }

    private AccountType getRequiredAccountType(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        if (requestUri.startsWith("/api/customer/")) {
            return AccountType.CUSTOMER;
        }
        if (requestUri.startsWith("/api/extranet/")) {
            return AccountType.EXTRANET;
        }
        return null;
    }

    private void writeErrorResponse(HttpServletResponse response, ExceptionType exceptionType) throws IOException {
        response.setStatus(exceptionType.status().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(
                response.getWriter(),
                ApiResponse.failure(exceptionType.code(), exceptionType.message(), null)
        );
    }
}
