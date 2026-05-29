package org.darksamus86.library.admin.service;

import org.darksamus86.library.admin.dto.response.AdminDashboardResponse;
import org.darksamus86.library.admin.dto.response.AdminUserResponse;
import org.darksamus86.library.book.repository.BookRepo;
import org.darksamus86.library.user.common.exceptions.UserNotFoundException;
import org.darksamus86.library.user.entity.Role;
import org.darksamus86.library.user.entity.User;
import org.darksamus86.library.user.entity.UserRole;
import org.darksamus86.library.user.repository.RoleRepo;
import org.darksamus86.library.user.repository.UserRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepo userRepository;

    @Mock
    private RoleRepo roleRepository;

    @Mock
    private BookRepo bookRepository;

    @InjectMocks
    private AdminService adminService;

    private User createUser(Long id, String username, boolean active, String... roleNames) {
        User user = new User();
        user.setId(id);
        user.setEmail(username + "@test.com");
        user.setUsername(username);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setIsActive(active);
        user.setIsEmailVerified(true);
        user.setCreatedAt(LocalDateTime.of(2026, 1, 1, 12, 0));
        user.setUpdatedAt(LocalDateTime.of(2026, 5, 28, 12, 0));

        for (String roleName : roleNames) {
            Role role = new Role();
            role.setId(roleName.equals("ROLE_ADMIN") ? 1L : 2L);
            role.setName(roleName);
            UserRole ur = UserRole.builder().user(user).role(role).build();
            user.getUserRoles().add(ur);
        }
        return user;
    }

    @Test
    @DisplayName("Should return dashboard stats")
    void getDashboard_ShouldReturnStats() {
        Role adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setName("ROLE_ADMIN");
        UserRole adminUr = UserRole.builder().role(adminRole).build();
        adminRole.setUserRoles(List.of(adminUr));

        Role userRole = new Role();
        userRole.setId(2L);
        userRole.setName("ROLE_USER");
        UserRole userUr1 = UserRole.builder().role(userRole).build();
        UserRole userUr2 = UserRole.builder().role(userRole).build();
        userRole.setUserRoles(List.of(userUr1, userUr2));

        when(userRepository.count()).thenReturn(5L);
        when(userRepository.countByIsActive(true)).thenReturn(4L);
        when(bookRepository.count()).thenReturn(20L);
        when(bookRepository.countByIsActive(true)).thenReturn(18L);
        when(roleRepository.findAll()).thenReturn(List.of(adminRole, userRole));

        AdminDashboardResponse result = adminService.getDashboard();

        assertThat(result.totalUsers()).isEqualTo(5);
        assertThat(result.activeUsers()).isEqualTo(4);
        assertThat(result.totalBooks()).isEqualTo(20);
        assertThat(result.activeBooks()).isEqualTo(18);
        assertThat(result.usersByRole()).containsEntry("ROLE_ADMIN", 1L);
        assertThat(result.usersByRole()).containsEntry("ROLE_USER", 2L);

        verify(userRepository).count();
        verify(userRepository).countByIsActive(true);
        verify(bookRepository).count();
        verify(bookRepository).countByIsActive(true);
        verify(roleRepository).findAll();
    }

    @Test
    @DisplayName("Should return paged users list")
    void getAllUsers_ShouldReturnPage() {
        User user = createUser(1L, "testuser", true, "ROLE_USER");
        PageRequest pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findAllWithRoles(pageable)).thenReturn(userPage);

        Page<AdminUserResponse> result = adminService.getAllUsers(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).username()).isEqualTo("testuser");
        assertThat(result.getContent().get(0).roles()).containsExactly("ROLE_USER");

        verify(userRepository).findAllWithRoles(pageable);
    }

    @Test
    @DisplayName("Should return user by id")
    void getUserById_ShouldReturnUser() {
        User user = createUser(1L, "admin", true, "ROLE_ADMIN");
        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(user));

        AdminUserResponse result = adminService.getUserById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.username()).isEqualTo("admin");
        assertThat(result.roles()).containsExactly("ROLE_ADMIN");
        assertThat(result.isActive()).isTrue();

        verify(userRepository).findByIdWithRoles(1L);
    }

    @Test
    @DisplayName("Should throw when user not found by id")
    void getUserById_WhenNotFound_ShouldThrow() {
        when(userRepository.findByIdWithRoles(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.getUserById(999L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByIdWithRoles(999L);
    }

    @Test
    @DisplayName("Should change user roles")
    void changeUserRoles_ShouldUpdateRoles() {
        User user = createUser(1L, "user", true, "ROLE_USER");
        Role adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setName("ROLE_ADMIN");

        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findAll()).thenReturn(List.of(adminRole, Role.builder().id(2L).name("ROLE_USER").build()));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        AdminUserResponse result = adminService.changeUserRoles(1L, Set.of("ROLE_ADMIN"));

        assertThat(result.roles()).containsExactly("ROLE_ADMIN");
        verify(userRepository).findByIdWithRoles(1L);
        verify(roleRepository).findAll();
        verify(roleRepository).findByName("ROLE_ADMIN");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw on invalid role name")
    void changeUserRoles_WhenRoleNotFound_ShouldThrow() {
        User user = createUser(1L, "user", true, "ROLE_USER");
        Role userRole = new Role();
        userRole.setId(2L);
        userRole.setName("ROLE_USER");

        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findAll()).thenReturn(List.of(userRole));

        assertThatThrownBy(() -> adminService.changeUserRoles(1L, Set.of("ROLE_SUPERADMIN")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ROLE_SUPERADMIN");

        verify(userRepository).findByIdWithRoles(1L);
        verify(roleRepository).findAll();
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should toggle user active status")
    void toggleUserStatus_ShouldChangeStatus() {
        User user = createUser(1L, "user", true, "ROLE_USER");
        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        AdminUserResponse result = adminService.toggleUserStatus(1L, false);

        assertThat(result.isActive()).isFalse();
        assertThat(user.getIsActive()).isFalse();
        verify(userRepository).findByIdWithRoles(1L);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw when toggling status of non-existent user")
    void toggleUserStatus_WhenNotFound_ShouldThrow() {
        when(userRepository.findByIdWithRoles(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.toggleUserStatus(999L, false))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByIdWithRoles(999L);
        verify(userRepository, never()).save(any());
    }
}
