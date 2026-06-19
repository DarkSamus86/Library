package org.darksamus86.library.user.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserUpdateDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should be valid with current password")
    void validRequest_ShouldHaveNoViolations() {
        UserUpdateDto dto = new UserUpdateDto(
                "new@test.com", "newuser", "newpass", "currentPass", "First", "Last"
        );

        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail when currentPassword is blank")
    void blankCurrentPassword_ShouldHaveViolation() {
        UserUpdateDto dto = new UserUpdateDto(
                "new@test.com", "newuser", "newpass", "", "First", "Last"
        );

        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail when username is too short")
    void shortUsername_ShouldHaveViolation() {
        UserUpdateDto dto = new UserUpdateDto(
                null, "ab", null, "currentPass", null, null
        );

        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }
}
