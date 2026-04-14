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
                book.getTitle(),
                book.getDescription(),
                book.getPrice(),
                book.getRentalPrice(),
                book.getDepositAmount(),
                book.getStockCount(),
                book.getPublishedYear()
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
                .price(request.price())
                .rentalPrice(request.rentalPrice())
                .depositAmount(request.depositAmount())
                .stockCount(request.stockCount())
                .publishedYear(request.publishedYear())
                .coverUrl(request.coverUrl())
                .isActive(true) // По умолчанию активна
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
        if (request.price() != null) {
            book.setPrice(request.price());
        }
        if (request.rentalPrice() != null) {
            book.setRentalPrice(request.rentalPrice());
        }
        if (request.depositAmount() != null) {
            book.setDepositAmount(request.depositAmount());
        }
        if (request.stockCount() != null) {
            book.setStockCount(request.stockCount());
        }
        if (request.publishedYear() != null) {
            book.setPublishedYear(request.publishedYear());
        }
        if (request.coverUrl() != null) {
            book.setCoverUrl(request.coverUrl());
        }
        if (request.isActive() != null) {
            book.setIsActive(request.isActive());
        }
    }
}
