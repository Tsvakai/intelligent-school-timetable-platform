package zw.co.upvalley.authservice.config.constants;

public final class ExceptionMessages {

    private ExceptionMessages() {
        // Private constructor to prevent instantiation
    }

    // User related messages
    public static final String USER_NOT_FOUND_ID = "User not found with id: ";
    public static final String USER_NOT_FOUND_USERNAME = "User not found with username: ";
    public static final String USER_ALREADY_EXISTS = "User already exists: ";
    public static final String INVALID_CREDENTIALS = "Invalid credentials";
    public static final String ACCOUNT_DISABLED = "Account is disabled";
    public static final String ACCOUNT_LOCKED = "Account is temporarily locked";

    // Token related messages
    public static final String TOKEN_REFRESH_FAILED = "Refresh token was expired. Please make a new signin request";
    public static final String TOKEN_REVOKED = "Refresh token was revoked. Please make a new signin request";
    public static final String TOKEN_INVALID = "Invalid or expired token";
}