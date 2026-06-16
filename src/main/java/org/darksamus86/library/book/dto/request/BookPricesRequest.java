package org.darksamus86.library.book.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BookPricesRequest(
        @NotNull(message = "Цена обязательна")
        @DecimalMin(value = "0.0", message = "Цена не может быть отрицательной")
        BigDecimal price,

        @DecimalMin(value = "0.0", message = "Цена аренды не может быть отрицательной")
        BigDecimal rentalPrice,

        @DecimalMin(value = "0.0", message = "Залог не может быть отрицательным")
        BigDecimal depositAmount
) {}
