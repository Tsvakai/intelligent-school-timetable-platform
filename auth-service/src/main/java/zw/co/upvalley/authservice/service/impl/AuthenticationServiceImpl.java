package zw.co.upvalley.authservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.upvalley.authservice.config.constants.ExceptionMessages;
import zw.co.upvalley.authservice.dto.request.AuthRequest;
import zw.co.upvalley.authservice.dto.request.PasswordChangeRequest;
import zw.co.upvalley.authservice.dto.request.RefreshTokenRequest;
import zw.co.upvalley.authservice.dto.request.UserRegistrationRequest;
import zw.co.upvalley.authservice.dto.response.AuthResponse;
import zw.co.upvalley.authservice.dto.response.CustomApiResponse;
import zw.co.upvalley.authservice.dto.response.UserResponse;
import zw.co.upvalley.authservice.exception.UserNotFoundException;
import zw.co.upvalley.authservice.persistence.entity.User;
import zw.co.upvalley.authservice.security.CustomUserDetails;
import zw.co.upvalley.authservice.service.helper.*;
import zw.co.upvalley.authservice.service.interfaces.AuthenticationService;
import zw.co.upvalley.authservice.service.interfaces.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TokenService tokenService;
    private final UserValidationService userValidationService;
    private final UserRegistrationService userRegistrationService;
    private final PasswordChangeService passwordChangeService;

    @Override
    @Transactional
    public AuthResponse authenticate(AuthRequest authRequest, String ipAddress, String userAgent) {
        try {
            // Perform authentication
            Authentication authentication = performAuthentication(authRequest);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get the authenticated user details and convert to User entity
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = getUserEntityFromDetails(userDetails);

            // Validate user account and handle success
            userValidationService.validateUserAccount(user);
            userValidationService.handleAuthenticationSuccess(user, ipAddress);

            // Generate tokens
            return tokenService.generateTokens(user, authRequest.getDeviceFingerprint(), ipAddress, userAgent);

        } catch (BadCredentialsException e) {
            userValidationService.handleAuthenticationFailure(authRequest.getUsername(), ipAddress);
            throw e;
        }
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest, String ipAddress, String userAgent) {
        return tokenService.refreshTokens(
                refreshTokenRequest.getRefreshToken(),
                refreshTokenRequest.getDeviceFingerprint(),
                ipAddress,
                userAgent
        );
    }

    @Override
    @Transactional
    public CustomApiResponse<UserResponse> registerUser(UserRegistrationRequest registrationRequest) {
        UserResponse userResponse = userRegistrationService.registerUser(registrationRequest);
        return CustomApiResponse.success("User registered successfully", userResponse);
    }

    @Override
    @Transactional
    public CustomApiResponse<Void> logout(String refreshToken, String deviceFingerprint) {
        // Implementation would go here
        log.info("User logged out successfully");
        return CustomApiResponse.success("Logged out successfully");
    }

    @Override
    @Transactional
    public CustomApiResponse<Void> logoutAllDevices(Long userId) {
        User user = getCurrentAuthenticatedUser();
        // Implementation would go here
        log.info("All devices logged out for user: {}", user.getUsername());
        return CustomApiResponse.success("All devices logged out successfully");
    }

    @Override
    @Transactional
    public CustomApiResponse<Void> changePassword(Long userId, PasswordChangeRequest passwordChangeRequest) {
        User user = getCurrentAuthenticatedUser();
        passwordChangeService.changePassword(user, passwordChangeRequest);
        log.info("Password changed successfully for user: {}", user.getUsername());
        return CustomApiResponse.success("Password changed successfully");
    }

    @Override
    public CustomApiResponse<Void> validateToken(String token) {
        // Implementation would go here
        return CustomApiResponse.success("Token is valid");
    }

    @Override
    public CustomApiResponse<UserResponse> getCurrentUser(Long userId) {
        log.info("Getting current user details for user ID: {}", userId);

        UserResponse user = userService.getUserById(userId);
        return CustomApiResponse.success("User details retrieved successfully", user);
    }

    // Private helper methods
    private Authentication performAuthentication(AuthRequest authRequest) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );
    }

    /**
     * Convert CustomUserDetails to User entity by looking up in database
     */
    private User getUserEntityFromDetails(CustomUserDetails userDetails) {
        return userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException(
                        ExceptionMessages.USER_NOT_FOUND_USERNAME + userDetails.getUsername()
                ));
    }

    /**
     * Get current authenticated user from SecurityContext
     */
    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotFoundException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            return getUserEntityFromDetails(userDetails);
        } else if (principal instanceof String) {
            // Handle case where principal is just a username string
            String username = (String) principal;
            return userService.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException(
                            ExceptionMessages.USER_NOT_FOUND_USERNAME + username
                    ));
        } else {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass().getName());
        }
    }
}