package zw.co.upvalley.authservice.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import zw.co.upvalley.authservice.dto.request.AuthRequest;
import zw.co.upvalley.authservice.dto.request.PasswordChangeRequest;
import zw.co.upvalley.authservice.dto.request.RefreshTokenRequest;
import zw.co.upvalley.authservice.dto.request.UserRegistrationRequest;
import zw.co.upvalley.authservice.dto.response.AuthResponse;
import zw.co.upvalley.authservice.dto.response.CustomApiResponse;
import zw.co.upvalley.authservice.dto.response.UserResponse;
import zw.co.upvalley.authservice.security.CustomUserDetails;
import zw.co.upvalley.authservice.service.interfaces.AuthenticationService;
import zw.co.upvalley.authservice.util.NetworkUtil;

import static zw.co.upvalley.authservice.config.constants.ControllerConstants.*;

@Slf4j
@RestController
@RequestMapping(AUTH_BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Authenticate user", description = "Login with username/email and password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "423", description = "Account locked")
    })
    @PostMapping(LOGIN)
    public ResponseEntity<CustomApiResponse<AuthResponse>> login(
            @Valid @RequestBody AuthRequest authRequest,
            HttpServletRequest request
    ) {
        log.info("Login attempt for user: {}", authRequest.getUsername());

        String ipAddress = NetworkUtil.getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        AuthResponse authResponse = authenticationService.authenticate(authRequest, ipAddress, userAgent);

        log.info("Login successful for user: {}", authResponse.getUser().getUsername());
        return ResponseEntity.ok(CustomApiResponse.success(LOGIN_SUCCESS, authResponse));
    }

    @Operation(summary = "Register new user", description = "Create a new user account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    @PostMapping(REGISTER)
    public ResponseEntity<CustomApiResponse<UserResponse>> register(
            @Valid @RequestBody UserRegistrationRequest registrationRequest
    ) {
        log.info("Registration attempt for username: {}", registrationRequest.getUsername());

        CustomApiResponse<UserResponse> response = authenticationService.registerUser(registrationRequest);

        log.info("User registered successfully: {}", registrationRequest.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Refresh access token", description = "Get new access token using refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "403", description = "Invalid or expired refresh token")
    })
    @PostMapping(REFRESH_TOKEN)
    public ResponseEntity<CustomApiResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest,
            HttpServletRequest request
    ) {
        log.info("Token refresh request");

        String ipAddress = NetworkUtil.getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        AuthResponse authResponse = authenticationService.refreshToken(refreshTokenRequest, ipAddress, userAgent);

        log.info("Token refreshed successfully for user: {}", authResponse.getUser().getUsername());
        return ResponseEntity.ok(CustomApiResponse.success("Token refreshed successfully", authResponse));
    }

    @Operation(summary = "Logout user", description = "Invalidate refresh token for current device")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logged out successfully")
    })
    @PostMapping(LOGOUT)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CustomApiResponse<Void>> logout(
            @RequestParam String refreshToken,
            @RequestParam(required = false) String deviceFingerprint
    ) {
        log.info("Logout request for refresh token");

        CustomUserDetails currentUser = getCurrentAuthenticatedUser();
        CustomApiResponse<Void> response = authenticationService.logout(refreshToken, deviceFingerprint);

        log.info("User logged out successfully: {}", currentUser.getUsername());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Logout all devices", description = "Invalidate all refresh tokens for user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All devices logged out successfully")
    })
    @PostMapping(LOGOUT_ALL)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CustomApiResponse<Void>> logoutAllDevices() {
        CustomUserDetails currentUser = getCurrentAuthenticatedUser();
        log.info("Logout all devices request for user: {}", currentUser.getUsername());

        CustomApiResponse<Void> response = authenticationService.logoutAllDevices(currentUser.getUserId());

        log.info("All devices logged out successfully for user: {}", currentUser.getUsername());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Change password", description = "Change current user's password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "401", description = "Current password is incorrect")
    })
    @PostMapping(CHANGE_PASSWORD)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CustomApiResponse<Void>> changePassword(
            @Valid @RequestBody PasswordChangeRequest passwordChangeRequest
    ) {
        CustomUserDetails currentUser = getCurrentAuthenticatedUser();
        log.info("Password change request for user: {}", currentUser.getUsername());

        CustomApiResponse<Void> response = authenticationService.changePassword(currentUser.getUserId(), passwordChangeRequest);

        log.info("Password changed successfully for user: {}", currentUser.getUsername());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Validate token", description = "Check if access token is valid")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token is valid"),
            @ApiResponse(responseCode = "401", description = "Token is invalid")
    })
    @GetMapping(VALIDATE_TOKEN)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CustomApiResponse<Void>> validateToken() {
        CustomUserDetails currentUser = getCurrentAuthenticatedUser();
        log.info("Token validation request for user: {}", currentUser.getUsername());

        // If we reach here, the token is valid (JWT filter already validated it)
        CustomApiResponse<Void> response = CustomApiResponse.success("Token is valid");

        log.info("Token validated successfully for user: {}", currentUser.getUsername());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get current user", description = "Get details of currently authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User details retrieved")
    })
    @GetMapping(ME)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CustomApiResponse<UserResponse>> getCurrentUser() {
        CustomUserDetails currentUser = getCurrentAuthenticatedUser();
        log.info("Get current user request for: {}", currentUser.getUsername());

        CustomApiResponse<UserResponse> response = authenticationService.getCurrentUser(currentUser.getUserId());

        log.info("Current user details retrieved for: {}", currentUser.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to get the current authenticated user from SecurityContext
     * This is the production-ready way to get the current user
     */
    private CustomUserDetails getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Attempt to access protected endpoint without authentication");
            throw new AccessDeniedException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            return (CustomUserDetails) principal;
        } else if (principal instanceof String) {
            // This should not happen in production with JWT, but handle gracefully
            log.warn("Unexpected principal type: String - {}", principal);
            throw new AccessDeniedException("Invalid authentication principal");
        } else {
            log.error("Unknown principal type: {}", principal.getClass().getName());
            throw new AccessDeniedException("Authentication error");
        }
    }

    /**
     * Alternative helper method if you prefer to get user ID directly
     */
    private Long getCurrentUserId() {
        return getCurrentAuthenticatedUser().getUserId();
    }

    /**
     * Alternative helper method if you prefer to get username directly
     */
    private String getCurrentUsername() {
        return getCurrentAuthenticatedUser().getUsername();
    }
}