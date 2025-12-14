package zw.co.upvalley.authservice.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import zw.co.upvalley.authservice.dto.request.PasswordChangeRequest;
import zw.co.upvalley.authservice.persistence.entity.User;
import zw.co.upvalley.authservice.service.interfaces.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordChangeService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public void changePassword(User user, PasswordChangeRequest passwordChangeRequest) {
        validateCurrentPassword(user, passwordChangeRequest.getCurrentPassword());
        updateUserPassword(user, passwordChangeRequest.getNewPassword());
    }

    private void validateCurrentPassword(User user, String currentPassword) {
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }
    }

    private void updateUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.findByUsername(user.getUsername()); // This will trigger save
    }
}