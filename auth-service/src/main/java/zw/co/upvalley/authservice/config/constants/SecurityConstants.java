package zw.co.upvalley.authservice.config.constants;

public final class SecurityConstants {

    private SecurityConstants() {
        // Private constructor to prevent instantiation
    }

    // Role Constants
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_TEACHER = "TEACHER";
    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_PARENT = "PARENT";
    public static final String ROLE_STAFF = "STAFF";

    // Security Configuration
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    public static final int ACCOUNT_LOCK_DURATION_MINUTES = 30;
    public static final int BCRYPT_STRENGTH = 12;

    // API Paths
    public static final String[] PUBLIC_ENDPOINTS = {
            "/auth/api/v1/auth/**",
            "/auth/api-docs/**",
            "/auth/swagger-ui/**",
            "/auth/swagger-ui.html",
            "/auth/management/health",
            "/auth/actuator/health"
    };

    public static final String ADMIN_ENDPOINTS = "/auth/api/v1/admin/**";
    public static final String TEACHER_ENDPOINTS = "/auth/api/v1/teacher/**";
    public static final String STUDENT_ENDPOINTS = "/auth/api/v1/student/**";
}