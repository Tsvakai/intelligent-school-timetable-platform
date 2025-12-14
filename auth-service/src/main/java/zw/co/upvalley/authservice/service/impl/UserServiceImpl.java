package zw.co.upvalley.authservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.upvalley.authservice.config.constants.ExceptionMessages;
import zw.co.upvalley.authservice.config.properties.SecurityProperties;
import zw.co.upvalley.authservice.dto.response.UserResponse;
import zw.co.upvalley.authservice.exception.UserNotFoundException;
import zw.co.upvalley.authservice.persistence.entity.User;
import zw.co.upvalley.authservice.persistence.repository.UserRepository;
import zw.co.upvalley.authservice.service.helper.UserServiceHelper;
import zw.co.upvalley.authservice.service.interfaces.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SecurityProperties securityProperties;

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsernameAndIsDeletedFalse(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailAndIsDeletedFalse(email);
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmailAndIsDeletedFalse(usernameOrEmail);
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = findUserById(userId);
        return UserServiceHelper.convertToUserResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAllByIsDeletedFalse().stream()
                .map(UserServiceHelper::convertToUserResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long userId, UserResponse userUpdate) {
        User user = findUserById(userId);
        updateUserFields(user, userUpdate);

        User savedUser = userRepository.save(user);
        log.info("User updated successfully: {}", savedUser.getUsername());

        return UserServiceHelper.convertToUserResponse(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = findUserById(userId);
        user.softDelete();
        userRepository.save(user);
        log.info("User soft deleted: {}", user.getUsername());
    }

    @Override
    @Transactional
    public void enableUser(Long userId) {
        User user = findUserById(userId);
        user.setIsEnabled(true);
        userRepository.save(user);
        log.info("User enabled: {}", user.getUsername());
    }

    @Override
    @Transactional
    public void disableUser(Long userId) {
        User user = findUserById(userId);
        user.setIsEnabled(false);
        userRepository.save(user);
        log.info("User disabled: {}", user.getUsername());
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsernameAndIsDeletedFalse(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailAndIsDeletedFalse(email);
    }

    @Override
    @Transactional
    public void recordLoginSuccess(User user, String ipAddress) {
        user.recordLogin();
        userRepository.save(user);
        log.info("Login success recorded for user: {} from IP: {}", user.getUsername(), ipAddress);
    }

    @Override
    @Transactional
    public void recordLoginFailure(String usernameOrEmail, String ipAddress) {
        Optional<User> userOpt = userRepository.findByUsernameOrEmailAndIsDeletedFalse(usernameOrEmail);

        if (userOpt.isPresent()) {
            handleFailedLogin(userOpt.get(), ipAddress);
        } else {
            log.warn("Login attempt failed for non-existent user: {} from IP: {}", usernameOrEmail, ipAddress);
        }
    }

    // Private helper methods
    private User findUserById(Long userId) {
        return userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessages.USER_NOT_FOUND_ID + userId));
    }

    private void updateUserFields(User user, UserResponse userUpdate) {
        if (userUpdate.getFirstName() != null) {
            user.setFirstName(userUpdate.getFirstName());
        }
        if (userUpdate.getLastName() != null) {
            user.setLastName(userUpdate.getLastName());
        }
        if (userUpdate.getPhoneNumber() != null) {
            user.setPhoneNumber(userUpdate.getPhoneNumber());
        }
        if (userUpdate.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(userUpdate.getProfilePictureUrl());
        }
    }

    private void handleFailedLogin(User user, String ipAddress) {
        user.incrementFailedLoginAttempts();

        if (user.getFailedLoginAttempts() >= securityProperties.getMaxLoginAttempts()) {
            lockUserAccount(user);
        }

        userRepository.save(user);
        log.warn("Login failure recorded for user: {} from IP: {}", user.getUsername(), ipAddress);
    }

    private void lockUserAccount(User user) {
        user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(securityProperties.getAccountLockDurationMinutes()));
        log.warn("Account locked for user: {} due to too many failed login attempts", user.getUsername());
    }
}