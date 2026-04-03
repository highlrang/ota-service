package com.ota_service.ota_service.service;

import com.ota_service.ota_service.dto.auth.LoginRequest;
import com.ota_service.ota_service.dto.auth.LoginResponse;
import com.ota_service.ota_service.entity.Extranet;
import com.ota_service.ota_service.enums.AccountStatus;
import com.ota_service.ota_service.exception.ApiException;
import com.ota_service.ota_service.exception.ExceptionType;
import com.ota_service.ota_service.repository.ExtranetRepository;
import com.ota_service.ota_service.security.AccountType;
import com.ota_service.ota_service.security.JwtProperties;
import com.ota_service.ota_service.security.JwtTokenProvider;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExtranetAuthService {

    private final ExtranetRepository extranetRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Extranet extranet = extranetRepository.findByEmail(request.email())
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_AUTH));

        validateActiveExtranet(extranet);
        validatePassword(request.password(), extranet.getPasswordHash());

        String accessToken = jwtTokenProvider.createAccessToken(
                extranet.getId(),
                extranet.getEmail(),
                extranet.getCode(),
                extranet.getName(),
                AccountType.EXTRANET
        );
        String refreshToken = jwtTokenProvider.createRefreshToken(
                extranet.getId(),
                extranet.getEmail(),
                extranet.getCode(),
                extranet.getName(),
                AccountType.EXTRANET
        );

        extranet.setAccessToken(accessToken);
        extranet.setRefreshToken(refreshToken);
        extranet.setRefreshTokenExpiredAt(LocalDateTime.now().plusSeconds(jwtProperties.refreshTokenExpirationSeconds()));
        extranet.setLastLoginAt(LocalDateTime.now());

        return new LoginResponse(
                extranet.getEmail(),
                extranet.getCode(),
                extranet.getName(),
                accessToken,
                refreshToken
        );
    }

    private void validateActiveExtranet(Extranet extranet) {
        if (extranet.getStatus() != AccountStatus.ACTIVE) {
            throw new ApiException(ExceptionType.ACCOUNT_INACTIVE, "비활성화된 판매자 계정입니다.");
        }
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new ApiException(ExceptionType.INVALID_AUTH);
        }
    }
}
