package zw.co.upvalley.authservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.upvalley.authservice.exception.TokenRefreshException;
import zw.co.upvalley.authservice.persistence.entity.RefreshToken;
import zw.co.upvalley.authservice.persistence.entity.User;
import zw.co.upvalley.authservice.persistence.repository.RefreshTokenRepository;
import zw.co.upvalley.authservice.service.interfaces.RefreshTokenService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user, String deviceFingerprint, String ipAddress, String userAgent) {
        // Revoke existing token for same device
        revokeUserTokensByDevice(user, deviceFingerprint);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(generateToken())
                .user(user)
                .expiryDate(LocalDateTime.now().plusSeconds(604800)) // 7 days
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .deviceFingerprint(deviceFingerprint)
                .isRevoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByTokenAndIsDeletedFalse(token);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }

        if (token.getIsRevoked()) {
            throw new TokenRefreshException(token.getToken(), "Refresh token was revoked. Please make a new signin request");
        }

        return token;
    }

    @Override
    @Transactional
    public void revokeToken(String token) {
        refreshTokenRepository.findByTokenAndIsDeletedFalse(token)
                .ifPresent(refreshToken -> {
                    refreshToken.revoke();
                    refreshTokenRepository.save(refreshToken);
                    log.info("Refresh token revoked for user: {}", refreshToken.getUser().getUsername());
                });
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.revokeAllUserTokens(user);
        log.info("All refresh tokens revoked for user: {}", user.getUsername());
    }

    @Override
    @Transactional
    public void revokeUserTokensByDevice(User user, String deviceFingerprint) {
        refreshTokenRepository.revokeUserTokensByDevice(user, deviceFingerprint);
        log.info("Refresh tokens revoked for user: {} on device: {}", user.getUsername(), deviceFingerprint);
    }

    @Override
    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteAllExpiredSince(LocalDateTime.now());
        log.info("Expired refresh tokens cleanup completed");
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}