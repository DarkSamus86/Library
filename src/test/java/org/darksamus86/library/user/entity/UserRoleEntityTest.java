package org.darksamus86.library.user.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRoleEntityTest {

    @Test
    @DisplayName("Should build UserRole with builder")
    void builder_ShouldCreateUserRole() {
        User user = new User();
        user.setId(1L);
        Role role = new Role();
        role.setId(2L);

        UserRole userRole = UserRole.builder().user(user).role(role).build();

        assertThat(userRole.getUser()).isEqualTo(user);
        assertThat(userRole.getRole()).isEqualTo(role);
    }

    @Test
    @DisplayName("Should create with all args constructor")
    void allArgsConstructor_ShouldCreateUserRole() {
        User user = new User();
        user.setId(1L);
        Role role = new Role();
        role.setId(2L);

        UserRole userRole = new UserRole(10L, user, role);

        assertThat(userRole.getId()).isEqualTo(10L);
        assertThat(userRole.getUser()).isEqualTo(user);
        assertThat(userRole.getRole()).isEqualTo(role);
    }
}
