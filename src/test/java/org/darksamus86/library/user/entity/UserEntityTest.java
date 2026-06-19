package org.darksamus86.library.user.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserEntityTest {

    @Test
    @DisplayName("Should set timestamps and defaults on PrePersist")
    void onCreate_ShouldSetDefaults() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setUsername("testuser");
        user.setPasswordHash("hashed");

        user.onCreate();

        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
        assertThat(user.getIsActive()).isTrue();
        assertThat(user.getIsEmailVerified()).isFalse();
    }

    @Test
    @DisplayName("Should not override isActive if already set")
    void onCreate_ShouldNotOverrideIsActive() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setUsername("testuser");
        user.setPasswordHash("hashed");
        user.setIsActive(false);
        user.setIsEmailVerified(true);

        user.onCreate();

        assertThat(user.getIsActive()).isFalse();
        assertThat(user.getIsEmailVerified()).isTrue();
    }

    @Test
    @DisplayName("Should update updatedAt on PreUpdate")
    void onUpdate_ShouldUpdateTimestamp() {
        User user = new User();
        LocalDateTime before = LocalDateTime.now().minusHours(1);
        user.setUpdatedAt(before);

        user.onUpdate();

        assertThat(user.getUpdatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    @DisplayName("Should build user with builder")
    void builder_ShouldCreateUser() {
        User user = User.builder()
                .email("test@test.com")
                .username("testuser")
                .passwordHash("hashed")
                .firstName("Test")
                .lastName("User")
                .phone("1234567890")
                .isActive(true)
                .isEmailVerified(false)
                .build();

        assertThat(user.getEmail()).isEqualTo("test@test.com");
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPasswordHash()).isEqualTo("hashed");
        assertThat(user.getFirstName()).isEqualTo("Test");
        assertThat(user.getLastName()).isEqualTo("User");
        assertThat(user.getPhone()).isEqualTo("1234567890");
        assertThat(user.getIsActive()).isTrue();
        assertThat(user.getIsEmailVerified()).isFalse();
    }

    @Test
    @DisplayName("Should initialize collections by default")
    void collections_ShouldBeInitializedByDefault() {
        User user = new User();
        assertThat(user.getUserRoles()).isNotNull();
        assertThat(user.getPaymentMethods()).isNotNull();
    }
}
