package org.darksamus86.library.book.mapper;

import org.darksamus86.library.book.dto.request.CreateBookRequest;
import org.darksamus86.library.book.dto.request.UpdateBookRequest;
import org.darksamus86.library.book.dto.response.ResponseGetBook;
import org.darksamus86.library.book.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    // Entity → DTO
    public ResponseGetBook toResponse(Book book) {
        if (book == null) {
            return null;
        }

        return new ResponseGetBook(
                book.getId(),
                book.getTitle(),
                book.getDescription(),
                book.getIsbn(),
                book.getPricePurchase(),
                book.getPriceRental(),
                book.getDepositAmount(),
                book.getHasPhysical(),
                book.getHasDigital(),
                book.getPhysicalInventory(),
                book.getDigitalLicenses(),
                book.getIsAvailableForRent(),
                book.getIsAvailableForPurchase(),
                book.getPublishedYear(),
                book.getCoverImageUrl(),
                book.getTotalRentalsCount(),
                book.getTotalPurchasesCount()
        );
    }

    // Request DTO → Entity (для создания)
    public Book toEntity(CreateBookRequest request) {
        if (request == null) {
            return null;
        }

        return Book.builder()
                .title(request.title())
                .description(request.description())
                .isbn(request.isbn())
                .pricePurchase(request.pricePurchase())
                .priceRental(request.priceRental())
                .depositAmount(request.depositAmount())
                .physicalInventory(request.physicalInventory())
                .digitalLicenses(request.digitalLicenses() != null ? request.digitalLicenses() : -1)
                .hasPhysical(request.hasPhysical() != null ? request.hasPhysical() : true)
                .hasDigital(request.hasDigital() != null ? request.hasDigital() : true)
                .isAvailableForRent(true)
                .isAvailableForPurchase(true)
                .totalRentalsCount(0)
                .totalPurchasesCount(0)
                .publishedYear(request.publishedYear())
                .coverImageUrl(request.coverImageUrl())
                .isActive(true)
                .version(0)
                .build();
    }

    // Request DTO → Entity (для обновления)
    public void updateEntityFromRequest(UpdateBookRequest request, Book book) {
        if (request == null || book == null) {
            return;
        }

        if (request.title() != null) {
            book.setTitle(request.title());
        }
        if (request.description() != null) {
            book.setDescription(request.description());
        }
        if (request.isbn() != null) {
            book.setIsbn(request.isbn());
        }
        if (request.pricePurchase() != null) {
            book.setPricePurchase(request.pricePurchase());
        }
        if (request.priceRental() != null) {
            book.setPriceRental(request.priceRental());
        }
        if (request.depositAmount() != null) {
            book.setDepositAmount(request.depositAmount());
        }
        if (request.physicalInventory() != null) {
            book.setPhysicalInventory(request.physicalInventory());
        }
        if (request.digitalLicenses() != null) {
            book.setDigitalLicenses(request.digitalLicenses());
        }
        if (request.hasPhysical() != null) {
            book.setHasPhysical(request.hasPhysical());
        }
        if (request.hasDigital() != null) {
            book.setHasDigital(request.hasDigital());
        }
        if (request.isAvailableForRent() != null) {
            book.setIsAvailableForRent(request.isAvailableForRent());
        }
        if (request.isAvailableForPurchase() != null) {
            book.setIsAvailableForPurchase(request.isAvailableForPurchase());
        }
        if (request.publishedYear() != null) {
            book.setPublishedYear(request.publishedYear());
        }
        if (request.coverImageUrl() != null) {
            book.setCoverImageUrl(request.coverImageUrl());
        }
        if (request.isActive() != null) {
            book.setIsActive(request.isActive());
        }
    }
}
