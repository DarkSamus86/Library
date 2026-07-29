package org.darksamus86.library.user.mapper;

import lombok.extern.slf4j.Slf4j;
import org.darksamus86.library.user.dto.request.UpdateProfileRequest;
import org.darksamus86.library.user.dto.request.UserRegistrationDto;
import org.darksamus86.library.user.dto.response.UserResponseDto;
import org.darksamus86.library.user.entity.Role;
import org.darksamus86.library.user.entity.User;
import org.darksamus86.library.user.entity.UserRole;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserMapper {

    /**
     * JPA Entity -> DTO (для ответов API)
     */
    public UserResponseDto toResponse(User user) {
        if (user == null) return null;

        Set<String> roles = user.getUserRoles() == null ? Set.of() :
                user.getUserRoles().stream()
                        .map(UserRole::getRole)
                        .map(Role::getName)
                        .collect(Collectors.toSet());

        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getIsActive(),
                user.getIsEmailVerified(),
                roles
        );
    }

    /**
     * DTO -> JPA Entity (для регистрации)
     * ⚠️ passwordHash НЕ маппится здесь. Хеширование делается в сервисе.
     */
    public User toEntity(UserRegistrationDto dto) {
        if (dto == null) return null;

        return User.builder()
                .email(dto.email())
                .username(dto.username())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                // isActive / isEmailVerified установит @PrePersist
                .build();
    }

    /**
     * Частичное обновление Entity из DTO (для профиля)
     * Обновляет только переданные не-null поля
     */
    public void applyProfileUpdates(UpdateProfileRequest request, User target) {
        if (request == null || target == null) return;

        if (request.email() != null) target.setEmail(request.email());
        if (request.username() != null) target.setUsername(request.username());
        if (request.firstName() != null) target.setFirstName(request.firstName());
        if (request.lastName() != null) target.setLastName(request.lastName());
    }
}
