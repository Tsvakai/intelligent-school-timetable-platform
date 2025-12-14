package zw.co.upvalley.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Password change request")
public class PasswordChangeRequest {

    @NotBlank(message = "Current password is required")
    @Schema(description = "Current password")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Schema(description = "New password")
    private String newPassword;
}