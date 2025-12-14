package zw.co.upvalley.authservice.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zw.co.upvalley.authservice.config.constants.SecurityConstants;
import zw.co.upvalley.authservice.dto.response.UserResponse;
import zw.co.upvalley.authservice.dto.response.CustomApiResponse;
import zw.co.upvalley.authservice.service.interfaces.UserService;

import java.util.List;

import static zw.co.upvalley.authservice.config.constants.ControllerConstants.*;

@Slf4j
@RestController
@RequestMapping(USERS_BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get all users", description = "Retrieve all users (Admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    @PreAuthorize("hasRole('" + SecurityConstants.ROLE_ADMIN + "')")
    public ResponseEntity<CustomApiResponse<List<UserResponse>>> getAllUsers() {
        log.info("Get all users request");

        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(CustomApiResponse.success("Users retrieved successfully", users));
    }

    @Operation(summary = "Get user by ID", description = "Retrieve user details by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(BY_ID)
    public ResponseEntity<CustomApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        log.info("Get user by ID request: {}", id);

        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(CustomApiResponse.success("User retrieved successfully", user));
    }

    @Operation(summary = "Update user", description = "Update user details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping(BY_ID)
    public ResponseEntity<CustomApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserResponse userUpdate
    ) {
        log.info("Update user request for ID: {}", id);

        UserResponse updatedUser = userService.updateUser(id, userUpdate);
        return ResponseEntity.ok(CustomApiResponse.success("User updated successfully", updatedUser));
    }

    @Operation(summary = "Delete user", description = "Soft delete user (Admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping(BY_ID)
    @PreAuthorize("hasRole('" + SecurityConstants.ROLE_ADMIN + "')")
    public ResponseEntity<CustomApiResponse<Void>> deleteUser(@PathVariable Long id) {
        log.info("Delete user request for ID: {}", id);

        userService.deleteUser(id);
        return ResponseEntity.ok(CustomApiResponse.success("User deleted successfully"));
    }

    @Operation(summary = "Enable user", description = "Enable a user account (Admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User enabled successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PatchMapping(BY_ID + "/enable")
    @PreAuthorize("hasRole('" + SecurityConstants.ROLE_ADMIN + "')")
    public ResponseEntity<CustomApiResponse<Void>> enableUser(@PathVariable Long id) {
        log.info("Enable user request for ID: {}", id);

        userService.enableUser(id);
        return ResponseEntity.ok(CustomApiResponse.success("User enabled successfully"));
    }

    @Operation(summary = "Disable user", description = "Disable a user account (Admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User disabled successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PatchMapping(BY_ID + "/disable")
    @PreAuthorize("hasRole('" + SecurityConstants.ROLE_ADMIN + "')")
    public ResponseEntity<CustomApiResponse<Void>> disableUser(@PathVariable Long id) {
        log.info("Disable user request for ID: {}", id);

        userService.disableUser(id);
        return ResponseEntity.ok(CustomApiResponse.success("User disabled successfully"));
    }
}