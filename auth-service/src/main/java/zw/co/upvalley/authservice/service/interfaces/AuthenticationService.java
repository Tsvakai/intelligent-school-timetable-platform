package zw.co.upvalley.authservice.service.interfaces;

import zw.co.upvalley.authservice.dto.request.AuthRequest;
import zw.co.upvalley.authservice.dto.request.PasswordChangeRequest;
import zw.co.upvalley.authservice.dto.request.RefreshTokenRequest;
import zw.co.upvalley.authservice.dto.request.UserRegistrationRequest;
import zw.co.upvalley.authservice.dto.response.AuthResponse;
import zw.co.upvalley.authservice.dto.response.CustomApiResponse;
import zw.co.upvalley.authservice.dto.response.UserResponse;

public interface AuthenticationService {

    AuthResponse authenticate(AuthRequest authRequest, String ipAddress, String userAgent);

    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest, String ipAddress, String userAgent);

    CustomApiResponse<UserResponse> registerUser(UserRegistrationRequest registrationRequest);

    CustomApiResponse<Void> logout(String refreshToken, String deviceFingerprint);

    CustomApiResponse<Void> logoutAllDevices(Long userId);

   CustomApiResponse<Void> changePassword(Long userId, PasswordChangeRequest passwordChangeRequest);

    CustomApiResponse<Void> validateToken(String token);

    CustomApiResponse<UserResponse> getCurrentUser(Long userId);
}