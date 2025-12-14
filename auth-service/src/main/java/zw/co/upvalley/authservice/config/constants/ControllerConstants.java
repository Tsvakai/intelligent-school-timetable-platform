package zw.co.upvalley.authservice.config.constants;

public final class ControllerConstants {

    private ControllerConstants() {}

    // API Paths
    public static final String API_V1 = "/api/v1";
    public static final String AUTH_BASE_PATH = API_V1 + "/auth";
    public static final String USERS_BASE_PATH = API_V1 + "/users";
    public static final String ADMIN_BASE_PATH = API_V1 + "/admin";

    // Endpoints
    public static final String LOGIN = "/login";
    public static final String REGISTER = "/register";
    public static final String REFRESH_TOKEN = "/refresh-token";
    public static final String LOGOUT = "/logout";
    public static final String LOGOUT_ALL = "/logout-all";
    public static final String CHANGE_PASSWORD = "/change-password";
    public static final String VALIDATE_TOKEN = "/validate-token";
    public static final String ME = "/me";
    public static final String BY_ID = "/{id}";

    // Messages
    public static final String LOGIN_SUCCESS = "Login successful";
    public static final String REGISTRATION_SUCCESS = "User registered successfully";
    public static final String LOGOUT_SUCCESS = "Logged out successfully";
    public static final String LOGOUT_ALL_SUCCESS = "All devices logged out successfully";
    public static final String PASSWORD_CHANGE_SUCCESS = "Password changed successfully";
    public static final String TOKEN_VALID = "Token is valid";
    public static final String USER_RETRIEVED = "User details retrieved successfully";
}
