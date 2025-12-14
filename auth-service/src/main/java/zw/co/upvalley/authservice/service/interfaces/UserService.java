package zw.co.upvalley.authservice.service.interfaces;

import zw.co.upvalley.authservice.dto.response.UserResponse;
import zw.co.upvalley.authservice.persistence.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    UserResponse getUserById(Long userId);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(Long userId, UserResponse userUpdate);

    void deleteUser(Long userId);

    void enableUser(Long userId);

    void disableUser(Long userId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void recordLoginSuccess(User user, String ipAddress);

    void recordLoginFailure(String usernameOrEmail, String ipAddress);

    User saveUser(User user);
}