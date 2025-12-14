package zw.co.upvalley.authservice.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.upvalley.authservice.config.constants.SecurityConstants;
import zw.co.upvalley.authservice.dto.request.UserRegistrationRequest;
import zw.co.upvalley.authservice.dto.response.UserResponse;
import zw.co.upvalley.authservice.exception.UserAlreadyExistsException;
import zw.co.upvalley.authservice.persistence.entity.User;
import zw.co.upvalley.authservice.service.interfaces.UserService;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse registerUser(UserRegistrationRequest registrationRequest) {
        validateUserRegistration(registrationRequest);
        User user = createUserFromRequest(registrationRequest);

        // Save the user using the service method
        User savedUser = userService.saveUser(user);

        log.info("User registered successfully: {}", savedUser.getUsername());
        return AuthenticationHelper.convertToUserResponse(savedUser);
    }

    private void validateUserRegistration(UserRegistrationRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
        }

        if (userService.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());
        }
    }

    private User createUserFromRequest(UserRegistrationRequest request) {
        Set<String> roles = determineUserRoles(request.getRoles());

        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .userType(request.getUserType())
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .build();
    }

    private Set<String> determineUserRoles(Set<String> requestedRoles) {
        Set<String> roles = new HashSet<>();

        if (requestedRoles != null && !requestedRoles.isEmpty()) {
            roles.addAll(requestedRoles);
        } else {
            // Set default role based on user type or application logic
            roles.add(SecurityConstants.ROLE_STUDENT);
        }

        return roles;
    }
}