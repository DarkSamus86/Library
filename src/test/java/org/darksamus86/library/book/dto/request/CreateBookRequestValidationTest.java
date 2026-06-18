package org.darksamus86.library.book.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CreateBookRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should be valid with all required fields")
    void validRequest_ShouldHaveNoViolations() {
        CreateBookRequest request = new CreateBookRequest(
                "Test Book", "Description", "1234567890",
                new BigDecimal("10.00"), new BigDecimal("2.00"), new BigDecimal("5.00"),
                5, 2023, "http://cover.url"
        );

        Set<ConstraintViolation<CreateBookRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail when title is blank")
    void blankTitle_ShouldHaveViolation() {
        CreateBookRequest request = new CreateBookRequest(
                "", "Description", "1234567890",
                new BigDecimal("10.00"), null, null, 5, 2023, null
        );

        Set<ConstraintViolation<CreateBookRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title"))).isTrue();
    }

    @Test
    @DisplayName("Should fail when price is null")
    void nullPrice_ShouldHaveViolation() {
        CreateBookRequest request = new CreateBookRequest(
                "Test Book", "Description", "1234567890",
                null, null, null, 5, 2023, null
        );

        Set<ConstraintViolation<CreateBookRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("price"))).isTrue();
    }

    @Test
    @DisplayName("Should fail when price is negative")
    void negativePrice_ShouldHaveViolation() {
        CreateBookRequest request = new CreateBookRequest(
                "Test Book", "Description", "1234567890",
                new BigDecimal("-1.00"), null, null, 5, 2023, null
        );

        Set<ConstraintViolation<CreateBookRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail when stockCount is null")
    void nullStockCount_ShouldHaveViolation() {
        CreateBookRequest request = new CreateBookRequest(
                "Test Book", "Description", "1234567890",
                new BigDecimal("10.00"), null, null, null, 2023, null
        );

        Set<ConstraintViolation<CreateBookRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail when stockCount is negative")
    void negativeStockCount_ShouldHaveViolation() {
        CreateBookRequest request = new CreateBookRequest(
                "Test Book", "Description", "1234567890",
                new BigDecimal("10.00"), null, null, -1, 2023, null
        );

        Set<ConstraintViolation<CreateBookRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail when title exceeds max length")
    void titleTooLong_ShouldHaveViolation() {
        String longTitle = "A".repeat(256);
        CreateBookRequest request = new CreateBookRequest(
                longTitle, "Description", "1234567890",
                new BigDecimal("10.00"), null, null, 5, 2023, null
        );

        Set<ConstraintViolation<CreateBookRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }
}
