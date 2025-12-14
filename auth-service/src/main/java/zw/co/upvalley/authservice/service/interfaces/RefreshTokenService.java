package zw.co.upvalley.authservice.service.interfaces;

import zw.co.upvalley.authservice.persistence.entity.RefreshToken;
import zw.co.upvalley.authservice.persistence.entity.User;

import java.util.Optional;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user, String deviceFingerprint, String ipAddress, String userAgent);

    Optional<RefreshToken> findByToken(String token);

    RefreshToken verifyExpiration(RefreshToken token);

    void revokeToken(String token);

    void revokeAllUserTokens(User user);

    void revokeUserTokensByDevice(User user, String deviceFingerprint);

    void cleanupExpiredTokens();
}