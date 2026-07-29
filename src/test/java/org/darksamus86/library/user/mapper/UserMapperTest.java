package org.darksamus86.library.user.mapper;

import org.darksamus86.library.user.dto.request.UserRegistrationDto;
import org.darksamus86.library.user.dto.request.UpdateProfileRequest;
import org.darksamus86.library.user.dto.response.UserResponseDto;
import org.darksamus86.library.user.entity.Role;
import org.darksamus86.library.user.entity.User;
import org.darksamus86.library.user.entity.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    @DisplayName("Should map User entity to UserResponseDto")
    void toResponse_ShouldMapCorrectly() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setUsername("testuser");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setIsActive(true);
        user.setIsEmailVerified(false);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        UserRole userRole = UserRole.builder().user(user).role(role).build();
        user.setUserRoles(List.of(userRole));

        UserResponseDto result = userMapper.toResponse(user);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("test@test.com");
        assertThat(result.username()).isEqualTo("testuser");
        assertThat(result.firstName()).isEqualTo("Test");
        assertThat(result.lastName()).isEqualTo("User");
        assertThat(result.isActive()).isTrue();
        assertThat(result.isEmailVerified()).isFalse();
        assertThat(result.roles()).containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("Should return null when user is null")
    void toResponse_WhenNullUser_ShouldReturnNull() {
        assertThat(userMapper.toResponse(null)).isNull();
    }

    @Test
    @DisplayName("Should return empty roles when user has no roles")
    void toResponse_WhenNoRoles_ShouldReturnEmptySet() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setUsername("testuser");
        user.setUserRoles(null);

        UserResponseDto result = userMapper.toResponse(user);

        assertThat(result.roles()).isEmpty();
    }

    @Test
    @DisplayName("Should map UserRegistrationDto to User entity")
    void toEntity_ShouldMapCorrectly() {
        UserRegistrationDto dto = new UserRegistrationDto("test@test.com", "testuser", "password123", "Test", "User");

        User result = userMapper.toEntity(dto);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@test.com");
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getFirstName()).isEqualTo("Test");
        assertThat(result.getLastName()).isEqualTo("User");
        assertThat(result.getPasswordHash()).isNull();
    }

    @Test
    @DisplayName("Should return null when DTO is null")
    void toEntity_WhenNullDto_ShouldReturnNull() {
        assertThat(userMapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("Should apply updates from UpdateProfileRequest to User")
    void applyProfileUpdates_ShouldUpdateFields() {
        User user = new User();
        user.setEmail("old@test.com");
        user.setUsername("olduser");
        user.setFirstName("Old");
        user.setLastName("Old");

        UpdateProfileRequest request = new UpdateProfileRequest(
                "new@test.com", "newuser", "New", "New"
        );

        userMapper.applyProfileUpdates(request, user);

        assertThat(user.getEmail()).isEqualTo("new@test.com");
        assertThat(user.getUsername()).isEqualTo("newuser");
        assertThat(user.getFirstName()).isEqualTo("New");
        assertThat(user.getLastName()).isEqualTo("New");
    }

    @Test
    @DisplayName("Should not update null fields")
    void applyProfileUpdates_WhenNullFields_ShouldNotUpdate() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setUsername("testuser");
        user.setFirstName("Test");
        user.setLastName("User");

        UpdateProfileRequest request = new UpdateProfileRequest(null, null, null, null);

        userMapper.applyProfileUpdates(request, user);

        assertThat(user.getEmail()).isEqualTo("test@test.com");
        assertThat(user.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should do nothing when DTO is null")
    void applyProfileUpdates_WhenNullRequest_ShouldDoNothing() {
        User user = new User();
        user.setEmail("test@test.com");

        userMapper.applyProfileUpdates(null, user);

        assertThat(user.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("Should do nothing when target is null")
    void applyProfileUpdates_WhenNullTarget_ShouldDoNothing() {
        UpdateProfileRequest request = new UpdateProfileRequest(
                "test@test.com", "testuser", "Test", "User"
        );

        userMapper.applyProfileUpdates(request, null);
    }
}
