package com.ota_service.ota_service.service;

import com.ota_service.ota_service.dto.auth.LoginRequest;
import com.ota_service.ota_service.dto.auth.LoginResponse;
import com.ota_service.ota_service.entity.Seller;
import com.ota_service.ota_service.enums.AccountStatus;
import com.ota_service.ota_service.exception.ApiException;
import com.ota_service.ota_service.exception.ExceptionType;
import com.ota_service.ota_service.repository.SellerRepository;
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
public class SellerAuthService {

    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Seller seller = sellerRepository.findByEmail(request.email())
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_AUTH));

        validateActiveSeller(seller);
        validatePassword(request.password(), seller.getPasswordHash());

        String accessToken = jwtTokenProvider.createAccessToken(
                seller.getId(),
                seller.getEmail(),
                seller.getCode(),
                seller.getName(),
                AccountType.SELLER
        );
        String refreshToken = jwtTokenProvider.createRefreshToken(
                seller.getId(),
                seller.getEmail(),
                seller.getCode(),
                seller.getName(),
                AccountType.SELLER
        );

        seller.setAccessToken(accessToken);
        seller.setRefreshToken(refreshToken);
        seller.setRefreshTokenExpiredAt(LocalDateTime.now().plusSeconds(jwtProperties.refreshTokenExpirationSeconds()));
        seller.setLastLoginAt(LocalDateTime.now());

        return new LoginResponse(
                seller.getEmail(),
                seller.getCode(),
                seller.getName(),
                accessToken,
                refreshToken
        );
    }

    private void validateActiveSeller(Seller seller) {
        if (seller.getStatus() != AccountStatus.ACTIVE) {
            throw new ApiException(ExceptionType.ACCOUNT_INACTIVE, "비활성화된 판매자 계정입니다.");
        }
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new ApiException(ExceptionType.INVALID_AUTH);
        }
    }
}
