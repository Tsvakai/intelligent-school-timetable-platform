package zw.co.upvalley.authservice.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import zw.co.upvalley.authservice.config.constants.ExceptionMessages;
import zw.co.upvalley.authservice.persistence.entity.User;
import zw.co.upvalley.authservice.service.interfaces.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserValidationService {

    private final UserService userService;

    public void validateUserAccount(User user) {
        if (user.isAccountLocked()) {
            throw new BadCredentialsException(ExceptionMessages.ACCOUNT_LOCKED);
        }

        if (!user.getIsEnabled()) {
            throw new BadCredentialsException(ExceptionMessages.ACCOUNT_DISABLED);
        }
    }

    public void handleAuthenticationFailure(String usernameOrEmail, String ipAddress) {
        userService.recordLoginFailure(usernameOrEmail, ipAddress);
    }

    public void handleAuthenticationSuccess(User user, String ipAddress) {
        userService.recordLoginSuccess(user, ipAddress);
    }
}