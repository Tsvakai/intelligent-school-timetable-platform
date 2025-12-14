package zw.co.upvalley.authservice.config.properties;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    @Positive(message = "Max login attempts must be positive")
    private int maxLoginAttempts = 5;

    @Positive(message = "Account lock duration must be positive")
    private int accountLockDurationMinutes = 30;
}