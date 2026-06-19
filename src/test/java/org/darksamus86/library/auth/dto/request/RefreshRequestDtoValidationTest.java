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

class RefreshRequestDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should be valid with refresh token")
    void validRequest_ShouldHaveNoViolations() {
        RefreshRequestDto dto = new RefreshRequestDto("some-refresh-token");

        Set<ConstraintViolation<RefreshRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail when refreshToken is blank")
    void blankRefreshToken_ShouldHaveViolation() {
        RefreshRequestDto dto = new RefreshRequestDto("");

        Set<ConstraintViolation<RefreshRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }
}
