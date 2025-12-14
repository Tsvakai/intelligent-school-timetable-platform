package zw.co.upvalley.authservice.service.interfaces;

import org.springframework.security.core.userdetails.UserDetails;
import zw.co.upvalley.authservice.persistence.entity.User;

public interface JwtService {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    boolean validateToken(String token);

    String extractUsername(String token);

    String extractTokenType(String token);

    boolean isTokenExpired(String token);

    UserDetails getUserDetailsFromToken(String token);
}