package org.darksamus86.library.book.dto.response;

import java.math.BigDecimal;

public record ResponseGetBook(
        Long id,
        String title,
        String description,
        BigDecimal pricePurchase,
        BigDecimal priceRental,
        BigDecimal depositAmount,
        Boolean hasPhysical,
        Boolean hasDigital,
        Integer physicalInventory,
        Integer digitalLicenses,
        Boolean isAvailableForRent,
        Boolean isAvailableForPurchase,
        Integer publishedYear,
        String coverImageUrl,
        Integer totalRentalsCount,
        Integer totalPurchasesCount
) {}
