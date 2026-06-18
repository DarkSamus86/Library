package org.darksamus86.library.auth.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ChangePasswordRequestDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should be valid with current and new password")
    void validRequest_ShouldHaveNoViolations() {
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto("currentPass", "newPassword123");

        Set<ConstraintViolation<ChangePasswordRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail when currentPassword is blank")
    void blankCurrentPassword_ShouldHaveViolation() {
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto("", "newPassword123");

        Set<ConstraintViolation<ChangePasswordRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail when newPassword is blank")
    void blankNewPassword_ShouldHaveViolation() {
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto("currentPass", "");

        Set<ConstraintViolation<ChangePasswordRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail when newPassword is too short")
    void shortNewPassword_ShouldHaveViolation() {
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto("currentPass", "short");

        Set<ConstraintViolation<ChangePasswordRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }
}
