package zw.co.upvalley.authservice.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.upvalley.authservice.config.properties.JwtProperties;
import zw.co.upvalley.authservice.dto.response.AuthResponse;
import zw.co.upvalley.authservice.exception.TokenRefreshException;
import zw.co.upvalley.authservice.persistence.entity.RefreshToken;
import zw.co.upvalley.authservice.persistence.entity.User;
import zw.co.upvalley.authservice.service.interfaces.JwtService;
import zw.co.upvalley.authservice.service.interfaces.RefreshTokenService;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final JwtProperties jwtProperties;

    @Transactional
    public AuthResponse generateTokens(User user, String deviceFingerprint, String ipAddress, String userAgent) {
        log.info("Generating tokens for user: {} from IP: {}", user.getUsername(), ipAddress);

        try {
            String accessToken = jwtService.generateAccessToken(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(
                    user, deviceFingerprint, ipAddress, userAgent
            );

            AuthResponse authResponse = AuthenticationHelper.buildAuthResponse(
                    user,
                    accessToken,
                    refreshToken.getToken(),
                    jwtProperties.getAccessTokenExpirationMs()
            );

            log.info("Tokens generated successfully for user: {}", user.getUsername());
            return authResponse;

        } catch (Exception e) {
            log.error("Failed to generate tokens for user: {}. Error: {}", user.getUsername(), e.getMessage());
            throw new RuntimeException("Token generation failed", e);
        }
    }

    @Transactional
    public AuthResponse refreshTokens(String refreshTokenValue, String deviceFingerprint, String ipAddress, String userAgent) {
        log.info("Refreshing tokens for device: {} from IP: {}", deviceFingerprint, ipAddress);

        try {
            return refreshTokenService.findByToken(refreshTokenValue)
                    .map(refreshToken -> {
                        // Verify the refresh token is valid and not expired
                        RefreshToken verifiedToken = refreshTokenService.verifyExpiration(refreshToken);

                        // Validate device fingerprint if provided
                        validateDeviceFingerprint(verifiedToken, deviceFingerprint);

                        // Generate new tokens
                        return generateTokens(
                                verifiedToken.getUser(),
                                deviceFingerprint,
                                ipAddress,
                                userAgent
                        );
                    })
                    .orElseThrow(() -> {
                        log.warn("Refresh token not found: {}", refreshTokenValue);
                        return new TokenRefreshException(refreshTokenValue,"Refresh token not found");
                    });

        } catch (TokenRefreshException e) {
            throw e; // Re-throw specific token exceptions
        } catch (Exception e) {
            log.error("Token refresh failed for token: {}. Error: {}", refreshTokenValue, e.getMessage());
            throw new TokenRefreshException("Token refresh failed", refreshTokenValue);
        }
    }

    /**
     * Validates device fingerprint for additional security
     */
    private void validateDeviceFingerprint(RefreshToken refreshToken, String currentDeviceFingerprint) {
        if (currentDeviceFingerprint != null && !currentDeviceFingerprint.isBlank()) {
            String storedFingerprint = refreshToken.getDeviceFingerprint();

            if (storedFingerprint != null && !storedFingerprint.equals(currentDeviceFingerprint)) {
                log.warn("Device fingerprint mismatch for user: {}. Stored: {}, Current: {}",
                        refreshToken.getUser().getUsername(), storedFingerprint, currentDeviceFingerprint);
                throw new TokenRefreshException("Device fingerprint mismatch", refreshToken.getToken());
            }
        }
    }

    /**
     * Revoke both access and refresh tokens (logout)
     */
    @Transactional
    public void revokeTokens(String refreshTokenValue, String deviceFingerprint) {
        log.info("Revoking tokens for device: {}", deviceFingerprint);

        try {
            refreshTokenService.findByToken(refreshTokenValue)
                    .ifPresentOrElse(
                            refreshToken -> {
                                // Verify device fingerprint if provided
                                if (deviceFingerprint != null && !deviceFingerprint.isBlank()) {
                                    validateDeviceFingerprint(refreshToken, deviceFingerprint);
                                }

                                // Revoke the refresh token
                                refreshTokenService.revokeToken(refreshTokenValue);
                                log.info("Tokens revoked successfully for user: {}",
                                        refreshToken.getUser().getUsername());
                            },
                            () -> log.warn("Refresh token not found for revocation: {}", refreshTokenValue)
                    );
        } catch (Exception e) {
            log.error("Failed to revoke tokens: {}", e.getMessage());
            throw new RuntimeException("Token revocation failed", e);
        }
    }

    /**
     * Validate access token
     */
    public boolean validateAccessToken(String accessToken) {
        try {
            return jwtService.validateToken(accessToken);
        } catch (Exception e) {
            log.error("Access token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract username from access token
     */
    public String extractUsernameFromToken(String accessToken) {
        try {
            return jwtService.extractUsername(accessToken);
        } catch (Exception e) {
            log.error("Failed to extract username from token: {}", e.getMessage());
            throw new RuntimeException("Token parsing failed", e);
        }
    }
}