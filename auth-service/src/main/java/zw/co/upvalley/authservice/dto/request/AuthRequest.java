package zw.co.upvalley.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication request containing login credentials")
public class AuthRequest {

    @NotBlank(message = "Username")
    @Schema(description = "Username")
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(description = "User password", example = "securePassword123")
    private String password;

    @Schema(description = "Device fingerprint for refresh token management", example = "device-12345")
    private String deviceFingerprint;
}