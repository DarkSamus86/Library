package org.darksamus86.library.book.dto.response;

import java.math.BigDecimal;

public record ResponseGetBook(
        Long id,
        String title,
        String description,
        BigDecimal price,
        BigDecimal rentalPrice,
        BigDecimal depositAmount,
        Integer stockCount,
        Integer publishedYear
) {}
