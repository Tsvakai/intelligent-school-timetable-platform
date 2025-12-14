package zw.co.upvalley.authservice.service.helper;

import lombok.experimental.UtilityClass;
import zw.co.upvalley.authservice.dto.response.UserResponse;
import zw.co.upvalley.authservice.persistence.entity.User;

@UtilityClass
public class UserServiceHelper {

    public UserResponse convertToUserResponse(User user) {
        return getUserResponse(user);
    }

    static UserResponse getUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .userType(user.getUserType())
                .profilePictureUrl(user.getProfilePictureUrl())
                .lastLoginAt(user.getLastLoginAt())
                .enabled(user.getIsEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }
}