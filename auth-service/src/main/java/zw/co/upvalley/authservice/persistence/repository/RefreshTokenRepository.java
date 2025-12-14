package zw.co.upvalley.authservice.persistence.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import zw.co.upvalley.authservice.persistence.entity.RefreshToken;
import zw.co.upvalley.authservice.persistence.entity.User;
import zw.co.upvalley.authservice.persistence.repository.base.BaseRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends BaseRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenAndIsDeletedFalse(String token);

    Optional<RefreshToken> findByUserAndIsDeletedFalse(User user);

    List<RefreshToken> findAllByUserAndIsDeletedFalse(User user);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :now AND rt.isDeleted = false")
    void deleteAllExpiredSince(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.user = :user AND rt.isDeleted = false")
    void revokeAllUserTokens(@Param("user") User user);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.user = :user AND rt.deviceFingerprint = :deviceFingerprint AND rt.isDeleted = false")
    void revokeUserTokensByDevice(@Param("user") User user, @Param("deviceFingerprint") String deviceFingerprint);

    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user = :user AND rt.deviceFingerprint = :deviceFingerprint AND rt.isDeleted = false AND rt.isRevoked = false")
    Optional<RefreshToken> findValidTokenByUserAndDevice(@Param("user") User user, @Param("deviceFingerprint") String deviceFingerprint);
}