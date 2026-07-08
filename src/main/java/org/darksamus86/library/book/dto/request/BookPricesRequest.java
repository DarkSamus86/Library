package org.darksamus86.library.book.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BookPricesRequest(
        @NotNull(message = "Цена покупки обязательна")
        @DecimalMin(value = "0.0", message = "Цена не может быть отрицательной")
        BigDecimal pricePurchase,

        @DecimalMin(value = "0.0", message = "Цена аренды не может быть отрицательной")
        BigDecimal priceRental,

        @DecimalMin(value = "0.0", message = "Залог не может быть отрицательным")
        BigDecimal depositAmount
) {}
