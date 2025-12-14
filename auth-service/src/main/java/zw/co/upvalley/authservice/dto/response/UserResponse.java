package zw.co.upvalley.authservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.upvalley.authservice.util.UserType;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User information response")
public class UserResponse {

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "Username", example = "johndoe")
    private String username;

    @Schema(description = "Email address", example = "john.doe@school.edu")
    private String email;

    @Schema(description = "User roles", example = "[\"STUDENT\"]")
    private Set<String> roles;

    @Schema(description = "First name", example = "John")
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @Schema(description = "Full name", example = "John Doe")
    private String fullName;

    @Schema(description = "Phone number", example = "+263771234567")
    private String phoneNumber;

    @Schema(description = "User type", example = "STUDENT")
    private UserType userType;

    @Schema(description = "Profile picture URL")
    private String profilePictureUrl;

    @Schema(description = "Last login timestamp")
    private LocalDateTime lastLoginAt;

    @Schema(description = "Account enabled status")
    private Boolean enabled;

    @Schema(description = "Account creation timestamp")
    private LocalDateTime createdAt;
}