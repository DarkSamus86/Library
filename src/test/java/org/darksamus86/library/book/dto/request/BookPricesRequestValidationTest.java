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

class BookPricesRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should be valid with price")
    void validRequest_ShouldHaveNoViolations() {
        BookPricesRequest request = new BookPricesRequest(
                new BigDecimal("10.00"), new BigDecimal("2.00"), new BigDecimal("5.00")
        );

        Set<ConstraintViolation<BookPricesRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail when price is null")
    void nullPrice_ShouldHaveViolation() {
        BookPricesRequest request = new BookPricesRequest(
                null, new BigDecimal("2.00"), new BigDecimal("5.00")
        );

        Set<ConstraintViolation<BookPricesRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("price"))).isTrue();
    }

    @Test
    @DisplayName("Should fail when price is negative")
    void negativePrice_ShouldHaveViolation() {
        BookPricesRequest request = new BookPricesRequest(
                new BigDecimal("-1.00"), null, null
        );

        Set<ConstraintViolation<BookPricesRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }
}
