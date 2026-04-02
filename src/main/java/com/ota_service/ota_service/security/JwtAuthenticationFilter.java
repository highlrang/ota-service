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
            "/api/seller/auth/login"
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

            AccountType requiredAccountType = getRequiredAccountType(request);
            if (requiredAccountType == null) {
                filterChain.doFilter(request, response);
                return;
            }

            String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith("Bearer ")) {
                writeErrorResponse(response, ExceptionType.UNAUTHORIZED);
                return;
            }

            String token = bearerToken.substring(7);
            if (!jwtTokenProvider.isValidToken(token)) {
                writeErrorResponse(response, ExceptionType.UNAUTHORIZED);
                return;
            }

            AuthenticatedAccount account = jwtTokenProvider.getAuthenticatedAccount(token);
            if (account.accountType() != requiredAccountType) {
                writeErrorResponse(response, ExceptionType.ACCESS_DENIED);
                return;
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            account,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + account.accountType().name()))
                    );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            MDC.put("userId", String.valueOf(account.accountId()));

            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
            MDC.remove("userId");
        }
    }

    private boolean isPublicRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return PUBLIC_URIS.contains(requestUri)
                || requestUri.startsWith("/swagger-ui/")
                || requestUri.startsWith("/v3/api-docs/");
    }

    private AccountType getRequiredAccountType(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        if (requestUri.startsWith("/api/customer/")) {
            return AccountType.CUSTOMER;
        }
        if (requestUri.startsWith("/api/seller/")) {
            return AccountType.SELLER;
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
