package org.darksamus86.library.book.service.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.darksamus86.library.book.dto.integration.OpenLibraryBook;
import org.darksamus86.library.book.entity.Author;
import org.darksamus86.library.book.entity.AuthorRole;
import org.darksamus86.library.book.entity.Book;
import org.darksamus86.library.book.repository.BookRepo;
import org.darksamus86.library.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookImportConsumer {

    private final BookRepo bookRepo;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_BOOK_IMPORT)
    @Transactional
    public void handleBookImport(OpenLibraryBook bookData) {
        log.debug("Received book for import: {}", bookData.title());

        String isbn = null;
        if (bookData.isbn() != null && !bookData.isbn().isEmpty()) {
            isbn = bookData.isbn().stream()
                    .filter(i -> i != null && !i.isBlank())
                    .findFirst()
                    .orElse(null);

            if (isbn != null) {
                isbn = isbn.replace("-", "").replace(" ", "").trim();
                if (bookRepo.findByIsbn(isbn).isPresent()) {
                    log.warn("Book with ISBN {} already exists, skipping", isbn);
                    return;
                }
            }
        }

        Book book = Book.builder()
                .title(bookData.title())
                .isbn(isbn)
                .publishedYear(bookData.first_publish_year())
                .coverUrl(bookData.cover_i() != null
                        ? "https://covers.openlibrary.org/b/id/" + bookData.cover_i() + "-L.jpg"
                        : null)
                .description("Imported from Open Library. Authors: "
                        + (bookData.author_name() != null
                        ? String.join(", ", bookData.author_name())
                        : "Unknown"))
                .price(BigDecimal.ZERO)
                .rentalPrice(BigDecimal.ZERO)
                .depositAmount(BigDecimal.ZERO)
                .stockCount(1)
                .isActive(true)
                .build();

        bookRepo.save(book);
        log.info("Imported book: {} (ISBN: {})", book.getTitle(), book.getIsbn());
    }
}
