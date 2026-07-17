package org.darksamus86.library.book.dto.response;

import java.math.BigDecimal;

public record ResponseGetPartBook(
        String title,
        String description,
        BigDecimal pricePurchase,
        Integer publishedYear,
        String coverImageUrl
) {
}
