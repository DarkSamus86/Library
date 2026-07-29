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

class UpdateProfileRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should accept valid profile fields")
    void validRequest_ShouldHaveNoViolations() {
        UpdateProfileRequest request = new UpdateProfileRequest(
                "new@test.com", "newuser", "First", "Last"
        );

        Set<ConstraintViolation<UpdateProfileRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should reject invalid email")
    void invalidEmail_ShouldHaveViolation() {
        UpdateProfileRequest request = new UpdateProfileRequest(
                "invalid-email", "newuser", null, null
        );

        Set<ConstraintViolation<UpdateProfileRequest>> violations = validator.validate(request);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("email");
    }

    @Test
    @DisplayName("Should reject short username")
    void shortUsername_ShouldHaveViolation() {
        UpdateProfileRequest request = new UpdateProfileRequest(
                null, "ab", null, null
        );

        Set<ConstraintViolation<UpdateProfileRequest>> violations = validator.validate(request);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("username");
    }

    @Test
    @DisplayName("Should reject username longer than fifty characters")
    void longUsername_ShouldHaveViolation() {
        UpdateProfileRequest request = new UpdateProfileRequest(
                null, "u".repeat(51), null, null
        );

        Set<ConstraintViolation<UpdateProfileRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("username");
    }

    @Test
    @DisplayName("Should accept an empty partial update")
    void emptyPartialUpdate_ShouldHaveNoViolations() {
        UpdateProfileRequest request = new UpdateProfileRequest(
                null, null, null, null
        );

        assertThat(validator.validate(request)).isEmpty();
    }
}
