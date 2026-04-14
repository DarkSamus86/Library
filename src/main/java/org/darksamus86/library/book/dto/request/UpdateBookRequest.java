package org.darksamus86.library.book.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public record UpdateBookRequest(
        String title,
        String description,
        String isbn,

        @DecimalMin(value = "0.0", message = "Цена не может быть отрицательной")
        BigDecimal price,

        @DecimalMin(value = "0.0", message = "Цена аренды не может быть отрицательной")
        BigDecimal rentalPrice,

        @DecimalMin(value = "0.0", message = "Залог не может быть отрицательным")
        BigDecimal depositAmount,

        @Min(value = 0, message = "Количество не может быть отрицательным")
        Integer stockCount,

        @Min(value = 1000, message = "Некорректный год издания")
        @Max(value = 2099, message = "Некорректный год издания")
        Integer publishedYear,

        String coverUrl,

        Boolean isActive
) {}
