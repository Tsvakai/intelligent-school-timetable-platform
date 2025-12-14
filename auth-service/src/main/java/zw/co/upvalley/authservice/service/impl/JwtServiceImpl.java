package zw.co.upvalley.authservice.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import zw.co.upvalley.authservice.config.properties.JwtProperties;
import zw.co.upvalley.authservice.persistence.entity.User;
import zw.co.upvalley.authservice.service.interfaces.JwtService;
import zw.co.upvalley.authservice.service.interfaces.UserService;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

import static zw.co.upvalley.authservice.config.constants.JwtConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;
    private final UserService userService;

    @Override
    public String generateAccessToken(User user) {
        return buildToken(user, jwtProperties.getAccessTokenExpirationMs(), ACCESS_TOKEN_TYPE);
    }

    @Override
    public String generateRefreshToken(User user) {
        return buildToken(user, jwtProperties.getRefreshTokenExpirationMs(), REFRESH_TOKEN_TYPE);
    }

    private String buildToken(User user, long expiration, String tokenType) {
        Instant now = Instant.now();
        Instant expirationTime = now.plusMillis(expiration);

        return Jwts.builder()
                .claim(USER_ID_CLAIM, user.getId())
                .claim(EMAIL_CLAIM, user.getEmail())
                .claim(ROLES_CLAIM, user.getRoles())
                .claim(USER_TYPE_CLAIM, user.getUserType())
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .subject(user.getUsername())
                .issuer(jwtProperties.getIssuer())
                .audience().add(jwtProperties.getAudience()).and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(expirationTime))
                .signWith(getSignInKey())
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token) && ACCESS_TOKEN_TYPE.equals(extractTokenType(token));
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get(TOKEN_TYPE_CLAIM, String.class));
    }

    @Override
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @Override
    public UserDetails getUserDetailsFromToken(String token) {
        String username = extractUsername(token);
        return userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username))
                .toUserDetails();
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}