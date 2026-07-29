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

class ChangePasswordRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should accept valid passwords")
    void validRequest_ShouldHaveNoViolations() {
        ChangePasswordRequest request = new ChangePasswordRequest(
                "currentPassword", "newPassword123"
        );

        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should reject blank current password")
    void blankCurrentPassword_ShouldHaveViolation() {
        ChangePasswordRequest request = new ChangePasswordRequest("", "newPassword123");

        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("currentPassword");
    }

    @Test
    @DisplayName("Should reject short new password")
    void shortNewPassword_ShouldHaveViolation() {
        ChangePasswordRequest request = new ChangePasswordRequest("currentPassword", "short");

        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("newPassword");
    }

    @Test
    @DisplayName("Should reject blank new password")
    void blankNewPassword_ShouldHaveViolation() {
        ChangePasswordRequest request = new ChangePasswordRequest(
                "currentPassword", ""
        );

        Set<ConstraintViolation<ChangePasswordRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("newPassword");
    }
}
