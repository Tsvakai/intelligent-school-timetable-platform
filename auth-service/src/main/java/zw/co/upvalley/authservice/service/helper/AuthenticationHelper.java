package zw.co.upvalley.authservice.service.helper;

import zw.co.upvalley.authservice.dto.response.AuthResponse;
import zw.co.upvalley.authservice.dto.response.UserResponse;
import zw.co.upvalley.authservice.persistence.entity.User;

public class AuthenticationHelper {
    private AuthenticationHelper() {}
    public static AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken, Long expiresIn) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn != null ? expiresIn : 900000L)
                .user(convertToUserResponse(user))
                .build();
    }

    public  static UserResponse convertToUserResponse(User user) {
        return UserServiceHelper.getUserResponse(user);
    }
}