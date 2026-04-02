package com.ota_service.ota_service.service;

import com.ota_service.ota_service.dto.auth.LoginRequest;
import com.ota_service.ota_service.dto.auth.LoginResponse;
import com.ota_service.ota_service.entity.User;
import com.ota_service.ota_service.exception.ApiException;
import com.ota_service.ota_service.exception.ExceptionType;
import com.ota_service.ota_service.repository.UserRepository;
import com.ota_service.ota_service.security.AccountType;
import com.ota_service.ota_service.security.JwtTokenProvider;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_AUTH));

        validateActiveUser(user);
        validatePassword(request.password(), user.getPasswordHash());

        String accessToken = jwtTokenProvider.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getName(),
                AccountType.CUSTOMER
        );

        user.setAccessToken(accessToken);
        user.setRefreshToken(UUID.randomUUID().toString());
        user.setRefreshTokenExpiredAt(LocalDateTime.now().plusDays(7));
        user.setLastLoginAt(LocalDateTime.now());

        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                AccountType.CUSTOMER,
                accessToken
        );
    }

    private void validateActiveUser(User user) {
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new ApiException(ExceptionType.ACCOUNT_INACTIVE, "비활성화된 고객 계정입니다.");
        }
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new ApiException(ExceptionType.INVALID_AUTH);
        }
    }
}
