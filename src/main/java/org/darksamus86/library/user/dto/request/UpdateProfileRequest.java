package org.darksamus86.library.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Email String email,
        @Size(min = 3, max = 50) String username,
        String firstName,
        String lastName
) {
}
