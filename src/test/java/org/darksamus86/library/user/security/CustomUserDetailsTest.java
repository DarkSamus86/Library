package org.darksamus86.library.user.security;

import org.darksamus86.library.user.entity.Role;
import org.darksamus86.library.user.entity.User;
import org.darksamus86.library.user.entity.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CustomUserDetailsTest {

    @Test
    @DisplayName("Should create CustomUserDetails from User entity")
    void constructor_ShouldCreateFromUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPasswordHash("hashedPassword");
        user.setIsActive(true);
        user.setIsEmailVerified(true);

        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("ROLE_USER");
        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("ROLE_ADMIN");

        UserRole ur1 = UserRole.builder().user(user).role(role1).build();
        UserRole ur2 = UserRole.builder().user(user).role(role2).build();
        user.setUserRoles(List.of(ur1, ur2));

        CustomUserDetails details = new CustomUserDetails(user);

        assertThat(details.getId()).isEqualTo(1L);
        assertThat(details.getUsername()).isEqualTo("testuser");
        assertThat(details.getPassword()).isEqualTo("hashedPassword");
        assertThat(details.isEnabled()).isTrue();
        assertThat(details.getAuthorities())
                .extracting("authority")
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    @DisplayName("Should return empty authorities when user has no roles")
    void constructor_WhenNoRoles_ShouldReturnEmptyAuthorities() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPasswordHash("hashed");
        user.setIsActive(true);
        user.setIsEmailVerified(false);
        user.setUserRoles(List.of());

        CustomUserDetails details = new CustomUserDetails(user);

        assertThat(details.getAuthorities()).isEmpty();
    }

    @Test
    @DisplayName("Should return disabled when user is inactive")
    void constructor_WhenInactive_ShouldBeDisabled() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPasswordHash("hashed");
        user.setIsActive(false);
        user.setIsEmailVerified(false);
        user.setUserRoles(List.of());

        CustomUserDetails details = new CustomUserDetails(user);

        assertThat(details.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Should return account non locked when user is active")
    void constructor_WhenActive_ShouldBeAccountNonLocked() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPasswordHash("hashed");
        user.setIsActive(true);
        user.setIsEmailVerified(false);
        user.setUserRoles(List.of());

        CustomUserDetails details = new CustomUserDetails(user);

        assertThat(details.isAccountNonLocked()).isTrue();
    }

    @Test
    @DisplayName("Should keep account non locked when user is inactive")
    void constructor_WhenInactive_ShouldRemainAccountNonLocked() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPasswordHash("hashed");
        user.setIsActive(false);
        user.setIsEmailVerified(false);
        user.setUserRoles(List.of());

        CustomUserDetails details = new CustomUserDetails(user);

        assertThat(details.isAccountNonLocked()).isTrue();
    }

    @Test
    @DisplayName("Should return account non expired always true")
    void constructor_ShouldAlwaysBeAccountNonExpired() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPasswordHash("hashed");
        user.setIsActive(true);
        user.setIsEmailVerified(false);
        user.setUserRoles(List.of());

        CustomUserDetails details = new CustomUserDetails(user);

        assertThat(details.isAccountNonExpired()).isTrue();
    }

    @Test
    @DisplayName("Should return credentials non expired always true")
    void constructor_ShouldAlwaysBeCredentialsNonExpired() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPasswordHash("hashed");
        user.setIsActive(true);
        user.setIsEmailVerified(false);
        user.setUserRoles(List.of());

        CustomUserDetails details = new CustomUserDetails(user);

        assertThat(details.isCredentialsNonExpired()).isTrue();
    }
}
