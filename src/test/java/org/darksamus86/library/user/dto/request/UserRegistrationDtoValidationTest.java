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

class UserRegistrationDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should be valid with all required fields")
    void validRequest_ShouldHaveNoViolations() {
        UserRegistrationDto dto = new UserRegistrationDto(
                "test@test.com", "testuser", "password123", "Test", "User"
        );

        Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail when email is blank")
    void blankEmail_ShouldHaveViolation() {
        UserRegistrationDto dto = new UserRegistrationDto(
                "", "testuser", "password123", "Test", "User"
        );

        Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email"))).isTrue();
    }

    @Test
    @DisplayName("Should fail when username is blank")
    void blankUsername_ShouldHaveViolation() {
        UserRegistrationDto dto = new UserRegistrationDto(
                "test@test.com", "", "password123", "Test", "User"
        );

        Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail when username is too short")
    void shortUsername_ShouldHaveViolation() {
        UserRegistrationDto dto = new UserRegistrationDto(
                "test@test.com", "ab", "password123", "Test", "User"
        );

        Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail when password is too short")
    void shortPassword_ShouldHaveViolation() {
        UserRegistrationDto dto = new UserRegistrationDto(
                "test@test.com", "testuser", "short", "Test", "User"
        );

        Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail when password is blank")
    void blankPassword_ShouldHaveViolation() {
        UserRegistrationDto dto = new UserRegistrationDto(
                "test@test.com", "testuser", "", "Test", "User"
        );

        Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }
}
