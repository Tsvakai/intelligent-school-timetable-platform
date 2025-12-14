package zw.co.upvalley.authservice.config.constants;

public final class JwtConstants {

    private JwtConstants() {
        // Private constructor to prevent instantiation
    }

    // JWT Claim Names
    public static final String TOKEN_TYPE_CLAIM = "tokenType";
    public static final String USER_ID_CLAIM = "userId";
    public static final String EMAIL_CLAIM = "email";
    public static final String ROLES_CLAIM = "roles";
    public static final String USER_TYPE_CLAIM = "userType";

    // Token Types
    public static final String ACCESS_TOKEN_TYPE = "ACCESS";
    public static final String REFRESH_TOKEN_TYPE = "REFRESH";

    // Token Prefix
    public static final String TOKEN_PREFIX = "Bearer ";

    // Authorization Header
    public static final String AUTHORIZATION_HEADER = "Authorization";

    // Default values
    public static final long DEFAULT_ACCESS_TOKEN_EXPIRATION_MS = 900000L; // 15 minutes
    public static final long DEFAULT_REFRESH_TOKEN_EXPIRATION_MS = 604800000L; // 7 days
}