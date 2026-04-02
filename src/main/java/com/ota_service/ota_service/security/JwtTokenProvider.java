package com.ota_service.ota_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private static final String CLAIM_ACCOUNT_ID = "accountId";
    private static final String CLAIM_ACCOUNT_NAME = "name";
    private static final String CLAIM_ACCOUNT_TYPE = "accountType";

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long accountId, String email, String name, AccountType accountType) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(jwtProperties.accessTokenExpirationSeconds());

        return Jwts.builder()
                .subject(email)
                .claim(CLAIM_ACCOUNT_ID, accountId)
                .claim(CLAIM_ACCOUNT_NAME, name)
                .claim(CLAIM_ACCOUNT_TYPE, accountType.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public AuthenticatedAccount getAuthenticatedAccount(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return new AuthenticatedAccount(
                claims.get(CLAIM_ACCOUNT_ID, Long.class),
                claims.getSubject(),
                claims.get(CLAIM_ACCOUNT_NAME, String.class),
                AccountType.valueOf(claims.get(CLAIM_ACCOUNT_TYPE, String.class))
        );
    }
}
