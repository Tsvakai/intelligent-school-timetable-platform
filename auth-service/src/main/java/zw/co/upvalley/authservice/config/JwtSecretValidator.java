package zw.co.upvalley.authservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import zw.co.upvalley.authservice.config.properties.JwtProperties;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtSecretValidator implements ApplicationListener<ApplicationReadyEvent> {

    private final Environment environment;
    private final JwtProperties jwtProperties;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        boolean isProd = isProductionProfile();

        if (isProd) {
            validateProductionSecret();
        } else {
            logDevelopmentWarning();
        }

        logJwtConfiguration();
    }

    private boolean isProductionProfile() {
        return Arrays.stream(environment.getActiveProfiles())
                .anyMatch(profile -> profile.equalsIgnoreCase("prod") ||
                        profile.equalsIgnoreCase("production"));
    }

    private void validateProductionSecret() {
        log.info("Validating JWT configuration for production environment...");

        String secret = jwtProperties.getSecret();

        // Check if secret is set
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException(
                    "JWT_SECRET environment variable must be set in production environment. " +
                            "Set it via: export JWT_SECRET='your-strong-secret' or in application-prod.yml"
            );
        }

        // Check minimum length
        if (secret.length() < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET must be at least 32 characters in production. " +
                            "Current length: " + secret.length() + " characters. " +
                            "Generate with: openssl rand -base64 48"
            );
        }

        // Check for weak patterns
        if (isWeakSecret(secret)) {
            throw new IllegalStateException(
                    "JWT_SECRET is too weak for production. " +
                            "Detected common weak patterns. Use a cryptographically strong random string."
            );
        }

        // Validate other JWT properties
        validateJwtProperties();

        log.info("JWT production validation passed - secret length: {}", secret.length());
    }

    private void validateJwtProperties() {
        if (jwtProperties.getAccessTokenExpirationMs() < 300000) { // 5 minutes minimum
            log.warn("Access token expiration is very short: {} ms",
                    jwtProperties.getAccessTokenExpirationMs());
        }

        if (jwtProperties.getRefreshTokenExpirationMs() < 86400000) { // 1 day minimum
            log.warn("Refresh token expiration is very short: {} ms",
                    jwtProperties.getRefreshTokenExpirationMs());
        }

        if (jwtProperties.getIssuer() == null || jwtProperties.getIssuer().trim().isEmpty()) {
            throw new IllegalStateException("JWT issuer must be configured");
        }
    }

    private void logDevelopmentWarning() {
        String secret = jwtProperties.getSecret();

        if (isWeakSecret(secret)) {
            log.warn("""
                DEVELOPMENT SECURITY WARNING
                Your JWT secret is weak and should not be used in production.
                
                Current secret issues:
                - Contains weak patterns: {}
                - Length: {} characters (minimum: 32)
                
                For production, generate a strong secret with:
                $ openssl rand -base64 48
                
                And set it as JWT_SECRET environment variable.
                """, detectWeakPatterns(secret), secret.length());
        }

        log.info("Development JWT configuration loaded");
    }

    private void logJwtConfiguration() {
        log.info("""
            JWT Configuration Summary
            Issuer: {}
            Audience: {}
            Access Token Expiration: {} minutes
            Refresh Token Expiration: {} days
            Secret Length: {} characters
            """,
                jwtProperties.getIssuer(),
                jwtProperties.getAudience(),
                jwtProperties.getAccessTokenExpirationMs() / 60000,
                jwtProperties.getRefreshTokenExpirationMs() / 86400000,
                jwtProperties.getSecret().length()
        );
    }

    private boolean isWeakSecret(String secret) {
        return secret.length() < 32 ||
                !detectWeakPatterns(secret).isEmpty();
    }

    private java.util.List<String> detectWeakPatterns(String secret) {
        java.util.List<String> patterns = new java.util.ArrayList<>();
        String lowerSecret = secret.toLowerCase();

        if (lowerSecret.contains("changethis")) patterns.add("'changethis'");
        if (lowerSecret.contains("default")) patterns.add("'default'");
        if (lowerSecret.contains("password")) patterns.add("'password'");
        if (lowerSecret.contains("secret")) patterns.add("'secret'");
        if (lowerSecret.contains("demo")) patterns.add("'demo'");
        if (lowerSecret.contains("example")) patterns.add("'example'");
        if (lowerSecret.contains("test")) patterns.add("'test'");
        if (lowerSecret.contains("dev")) patterns.add("'dev'");
        if (secret.equals("mySuperSecretKeyForJWTGenerationInProductionChangeThis")) {
            patterns.add("default template value");
        }
        return patterns;
    }
}