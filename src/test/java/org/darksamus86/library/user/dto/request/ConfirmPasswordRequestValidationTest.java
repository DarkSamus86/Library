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

class ConfirmPasswordRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should accept token and password with at least eight characters")
    void validRequest_ShouldHaveNoViolations() {
        ConfirmPasswordRequest request = new ConfirmPasswordRequest(
                "reset-token", "newPassword123"
        );

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    @DisplayName("Should reject blank confirmation token")
    void blankToken_ShouldHaveViolation() {
        ConfirmPasswordRequest request = new ConfirmPasswordRequest(
                "", "newPassword123"
        );

        Set<ConstraintViolation<ConfirmPasswordRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("token");
    }

    @Test
    @DisplayName("Should reject short new password")
    void shortPassword_ShouldHaveViolation() {
        ConfirmPasswordRequest request = new ConfirmPasswordRequest(
                "reset-token", "short"
        );

        Set<ConstraintViolation<ConfirmPasswordRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("newPassword");
    }
}
