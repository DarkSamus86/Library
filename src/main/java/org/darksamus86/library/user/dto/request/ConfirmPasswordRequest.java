package org.darksamus86.library.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConfirmPasswordRequest(
        @NotBlank String token,
        @NotBlank @Size(min = 8) String newPassword
) {

}
