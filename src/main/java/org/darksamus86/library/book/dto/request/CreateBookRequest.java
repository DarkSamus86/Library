package org.darksamus86.library.book.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateBookRequest(
        @NotBlank(message = "Название обязательно")
        @Size(max = 255, message = "Название не должно превышать 255 символов")
        String title,

        String description,

        @Size(max = 13, message = "ISBN должен содержать до 13 символов")
        String isbn,

        @NotNull(message = "Цена обязательна")
        @DecimalMin(value = "0.0", message = "Цена не может быть отрицательной")
        BigDecimal price,

        @DecimalMin(value = "0.0", message = "Цена аренды не может быть отрицательной")
        BigDecimal rentalPrice,

        @DecimalMin(value = "0.0", message = "Залог не может быть отрицательным")
        BigDecimal depositAmount,

        @NotNull(message = "Количество на складе обязательно")
        @Min(value = 0, message = "Количество не может быть отрицательным")
        Integer stockCount,

        @Min(value = 1000, message = "Некорректный год издания")
        @Max(value = 2099, message = "Некорректный год издания")
        Integer publishedYear,

        String coverUrl
) {}
