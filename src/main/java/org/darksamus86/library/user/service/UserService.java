package org.darksamus86.library.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.darksamus86.library.auth.service.TokenStorageService;
import org.darksamus86.library.user.common.exceptions.*;
import org.darksamus86.library.user.dto.request.UpdateProfileRequest;
import org.darksamus86.library.user.dto.request.UserRegistrationDto;
import org.darksamus86.library.user.dto.response.UserResponseDto;
import org.darksamus86.library.user.entity.Role;
import org.darksamus86.library.user.entity.User;
import org.darksamus86.library.user.entity.UserRole;
import org.darksamus86.library.user.mapper.UserMapper;
import org.darksamus86.library.user.repository.RoleRepo;
import org.darksamus86.library.user.repository.UserRepo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepo userRepository;
    private final RoleRepo roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final TokenStorageService tokenStorageService;

    public UserResponseDto register(UserRegistrationDto dto) {
        if (userRepository.existsByEmail(dto.email()))
            throw new EmailAlreadyExistsException(dto.email());
        if (userRepository.existsByUsername(dto.username()))
            throw new UsernameAlreadyExistsException(dto.username());

        User user = userMapper.toEntity(dto);
        user.setPasswordHash(passwordEncoder.encode(dto.password()));

        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RoleNotFoundException("ROLE_USER"));
        addRoleToUser(user, defaultRole);

        User saved = userRepository.save(user);
        log.info("User registered successfully: id={}", saved.getId());

        return userMapper.toResponse(saved);
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(id)));
        log.debug("Fetched user info: id={}", id);

        return userMapper.toResponse(user);
    }

    public UserResponseDto getCurrentUser() {
        String currentPrincipalName = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        log.debug("Fetched user info: name={}", currentPrincipalName);

        User user = userRepository.findByUsername(currentPrincipalName)
                .orElseThrow(() -> new UsernameNotFoundException("Logged-in user record not found"));


        return userMapper.toResponse(user);
    }

    @Transactional
    public void changePassword(
            Long userId,
            String currentPassword,
            String newPassword
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(userId.toString()));

        if (!passwordEncoder.matches(
                currentPassword,
                user.getPasswordHash()
        )) {
            throw new PasswordMismatchException();
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Update password of user with id: {}", userId);

        tokenStorageService.revokeToken(user.getUsername());
    }

    @Transactional
    public UserResponseDto updateProfile(
            Long currentUserId,
            UpdateProfileRequest request
    ) {
        User user = userRepository.findByIdWithRoles(currentUserId)
                .orElseThrow(() ->
                        new UsernameNotFoundException(currentUserId.toString()));

        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new EmailAlreadyExistsException(request.email());
            }
        }

        if (request.username() != null && !request.username().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.username())) {
                throw new UsernameAlreadyExistsException(request.username());
            }
        }

        userMapper.applyProfileUpdates(request, user);
        // Временно закоменчено до реализации сервиса уведомлений
        // user.setIsEmailVerified(false);

        log.info("Updated profile user: id={}", currentUserId);

        return userMapper.toResponse(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(String.valueOf(id));
        }

        if (userRepository.hasPaymentMethods(id)) {
            throw new UserDeletionForbiddenException(id, "User has active payment methods. Consider deactivation instead.");
        }

        userRepository.deleteById(id);
        log.info("User deleted by admin: id={}", id);
    }

    private void addRoleToUser(User user, Role role) {
        UserRole userRole = UserRole.builder().user(user).role(role).build();
        user.getUserRoles().add(userRole);
        role.getUserRoles().add(userRole);
    }
}
