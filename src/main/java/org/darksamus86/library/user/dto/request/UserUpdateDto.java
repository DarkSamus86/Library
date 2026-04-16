package org.darksamus86.library.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateDto(
        @Email String email,
        @Size(min = 3, max = 50) String username,
        String password,
        @NotBlank String currentPassword, // требуется при смене пароля
        String firstName,
        String lastName
) {}