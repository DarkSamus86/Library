package org.darksamus86.library.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.darksamus86.library.user.common.exceptions.*;
import org.darksamus86.library.user.dto.request.UserRegistrationDto;
import org.darksamus86.library.user.dto.request.UserUpdateDto;
import org.darksamus86.library.user.dto.response.UserResponseDto;
import org.darksamus86.library.user.entity.Role;
import org.darksamus86.library.user.entity.User;
import org.darksamus86.library.user.entity.UserRole;
import org.darksamus86.library.user.mapper.UserMapper;
import org.darksamus86.library.user.repository.RoleRepo;
import org.darksamus86.library.user.repository.UserRepo;
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

    // ✅ РЕГИСТРАЦИЯ
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

    // ✅ ПОЛУЧЕНИЕ ПОЛЬЗОВАТЕЛЯ
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(id)));
        log.debug("Fetched user info: id={}", id);
        return userMapper.toResponse(user);
    }

    // ✅ ОБНОВЛЕНИЕ ПРОФИЛЯ
    public UserResponseDto updateUser(Long id, UserUpdateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(id)));

        // Обязательная проверка текущего пароля
        if (!passwordEncoder.matches(dto.currentPassword(), user.getPasswordHash())) {
            throw new PasswordMismatchException();
        }

        boolean emailChanged = dto.email() != null && !dto.email().equals(user.getEmail());
        boolean usernameChanged = dto.username() != null && !dto.username().equals(user.getUsername());

        if (emailChanged) {
            if (userRepository.existsByEmail(dto.email()))
                throw new EmailAlreadyExistsException(dto.email());
            user.setEmail(dto.email());
            user.setIsEmailVerified(false); // Сброс верификации
            log.info("Email changed for userId={}", id);
        }

        if (usernameChanged) {
            if (userRepository.existsByUsername(dto.username()))
                throw new UsernameAlreadyExistsException(dto.username());
            user.setUsername(usernameChanged ? dto.username() : user.getUsername());
            log.info("Username changed for userId={}", id);
        }

        if (dto.password() != null) {
            user.setPasswordHash(passwordEncoder.encode(dto.password()));
            log.info("Password updated for userId={}", id);
        }

        // Обновляем остальные поля через маппер
        userMapper.applyUpdates(dto, user);

        User updated = userRepository.save(user);
        log.info("User profile updated: id={}", id);
        return userMapper.toResponse(updated);
    }

    // ✅ УДАЛЕНИЕ ПОЛЬЗОВАТЕЛЯ (только админ)
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(id)));

        // Проверка на наличие связанных данных (например, активные книги/платежи)
        if (!user.getPaymentMethods().isEmpty()) {
            throw new UserDeletionForbiddenException(id, "User has active payment methods. Consider deactivation instead.");
        }

        userRepository.delete(user);
        log.info("User deleted by admin: id={}", id);
    }

    /**
     * Только смена пароля (без побочной логики)
     */
    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        // Проверка текущего пароля
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new PasswordMismatchException();
        }

        // Установка нового пароля
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password changed for user: {}", username);
    }

    private void addRoleToUser(User user, Role role) {
        UserRole userRole = UserRole.builder().user(user).role(role).build();
        user.getUserRoles().add(userRole);
        role.getUserRoles().add(userRole);
    }
}