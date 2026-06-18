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

class LoginRequestDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should be valid with username and password")
    void validRequest_ShouldHaveNoViolations() {
        LoginRequestDto dto = new LoginRequestDto("testuser", "password123");

        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail when username is blank")
    void blankUsername_ShouldHaveViolation() {
        LoginRequestDto dto = new LoginRequestDto("", "password123");

        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail when password is blank")
    void blankPassword_ShouldHaveViolation() {
        LoginRequestDto dto = new LoginRequestDto("testuser", "");

        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }
}
