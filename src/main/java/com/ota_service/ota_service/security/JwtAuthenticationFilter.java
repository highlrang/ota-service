package com.ota_service.ota_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
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

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
                String token = bearerToken.substring(7);

                if (jwtTokenProvider.isValidToken(token)) {
                    AuthenticatedAccount account = jwtTokenProvider.getAuthenticatedAccount(token);
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
            }

            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("userId");
        }
    }
}
