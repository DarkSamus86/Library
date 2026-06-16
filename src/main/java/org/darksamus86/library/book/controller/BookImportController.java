package org.darksamus86.library.book.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.darksamus86.library.book.service.integration.BookImportProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books/import")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class BookImportController {

    private final BookImportProducer producer;

    @PostMapping
    public ResponseEntity<Void> importBooks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {

        log.info("Admin requested book import - query: {}, title: {}, author: {}, limit: {}", query, title, author, limit);

        if ((query == null || query.isBlank()) &&
                (title == null || title.isBlank()) &&
                (author == null || author.isBlank())) {
            log.warn("Missing search parameters for book import");
            return ResponseEntity.badRequest().build();
        }

        producer.importBooks(query, title, author, limit);
        return ResponseEntity.accepted().build();
    }
}
