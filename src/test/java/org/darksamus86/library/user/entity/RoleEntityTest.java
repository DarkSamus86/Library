package org.darksamus86.library.user.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoleEntityTest {

    @Test
    @DisplayName("Should build role with builder")
    void builder_ShouldCreateRole() {
        Role role = Role.builder()
                .id(1L)
                .name("ROLE_ADMIN")
                .description("Administrator role")
                .build();

        assertThat(role.getId()).isEqualTo(1L);
        assertThat(role.getName()).isEqualTo("ROLE_ADMIN");
        assertThat(role.getDescription()).isEqualTo("Administrator role");
    }

    @Test
    @DisplayName("Should initialize userRoles with empty list by default")
    void userRoles_ShouldBeInitializedByDefault() {
        Role role = new Role();
        assertThat(role.getUserRoles()).isNotNull();
        assertThat(role.getUserRoles()).isEmpty();
    }
}
