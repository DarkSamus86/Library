package org.darksamus86.library.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.darksamus86.library.admin.dto.response.AdminDashboardResponse;
import org.darksamus86.library.admin.dto.response.AdminUserResponse;
import org.darksamus86.library.book.repository.BookRepo;
import org.darksamus86.library.user.common.exceptions.RoleNotFoundException;
import org.darksamus86.library.user.common.exceptions.UserNotFoundException;
import org.darksamus86.library.user.entity.Role;
import org.darksamus86.library.user.entity.User;
import org.darksamus86.library.user.entity.UserRole;
import org.darksamus86.library.user.repository.RoleRepo;
import org.darksamus86.library.user.repository.UserRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepo userRepository;
    private final RoleRepo roleRepository;
    private final BookRepo bookRepository;

    public AdminDashboardResponse getDashboard() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActive(true);
        long totalBooks = bookRepository.count();
        long activeBooks = bookRepository.countByIsActive(true);

        List<Role> allRoles = roleRepository.findAll();
        Map<String, Long> usersByRole = new LinkedHashMap<>();
        for (Role role : allRoles) {
            long count = role.getUserRoles().size();
            usersByRole.put(role.getName(), count);
        }

        log.info("Admin dashboard fetched");
        return new AdminDashboardResponse(totalUsers, activeUsers, totalBooks, activeBooks, usersByRole);
    }

    public Page<AdminUserResponse> getAllUsers(Pageable pageable) {
        log.info("Admin fetching all users with pagination: {}", pageable);
        return userRepository.findAllWithRoles(pageable)
                .map(this::toAdminResponse);
    }

    public AdminUserResponse getUserById(Long id) {
        log.info("Admin fetching user by id: {}", id);
        User user = userRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(id)));
        return toAdminResponse(user);
    }

    @Transactional
    public AdminUserResponse changeUserRoles(Long userId, Set<String> newRoleNames) {
        log.info("Admin changing roles for userId={}: {}", userId, newRoleNames);

        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(userId)));

        Set<String> validRoleNames = roleRepository.findAll().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        for (String roleName : newRoleNames) {
            if (!validRoleNames.contains(roleName)) {
                throw new IllegalArgumentException("Role not found: " + roleName);
            }
        }

        user.getUserRoles().clear();

        for (String roleName : newRoleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RoleNotFoundException(roleName));
            UserRole userRole = UserRole.builder().user(user).role(role).build();
            user.getUserRoles().add(userRole);
        }

        userRepository.save(user);
        log.info("Roles updated for userId={}: {}", userId, newRoleNames);
        return toAdminResponse(user);
    }

    @Transactional
    public AdminUserResponse toggleUserStatus(Long userId, boolean isActive) {
        log.info("Admin {} user: userId={}", isActive ? "activating" : "deactivating", userId);

        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(userId)));

        user.setIsActive(isActive);
        userRepository.save(user);

        log.info("User {} status set to active={}", userId, isActive);
        return toAdminResponse(user);
    }

    private AdminUserResponse toAdminResponse(User user) {
        Set<String> roles = user.getUserRoles() == null ? Set.of() :
                user.getUserRoles().stream()
                        .map(UserRole::getRole)
                        .map(Role::getName)
                        .collect(Collectors.toSet());

        return new AdminUserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getIsActive(),
                user.getIsEmailVerified(),
                roles,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
