package zw.co.upvalley.authservice.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "app.security.jwt")
public class JwtProperties {

    @NotBlank(message = "JWT secret key is required")
    @Size(min = 32, message = "JWT secret must be at least 32 characters long for security")
    private String secret;

    @Positive(message = "Access token expiration must be positive")
    private long accessTokenExpirationMs = 900000; // 15 minutes

    @Positive(message = "Refresh token expiration must be positive")
    private long refreshTokenExpirationMs = 604800000; // 7 days

    @NotBlank(message = "JWT issuer is required")
    private String issuer = "istms-auth-service";

    @NotBlank(message = "JWT audience is required")
    private String audience = "istms-platform";
}