package org.darksamus86.library.book.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.darksamus86.library.book.dto.request.BookImportRequest;
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
    public ResponseEntity<Void> importBooks(@RequestBody BookImportRequest request) {
        log.info("Admin requested book import - query: {}, title: {}, author: {}, limit: {}",
                request.query(), request.title(), request.author(), request.limit());

        if ((request.query() == null || request.query().isBlank()) &&
                (request.title() == null || request.title().isBlank()) &&
                (request.author() == null || request.author().isBlank())) {
            log.warn("Missing search parameters for book import");
            return ResponseEntity.badRequest().build();
        }

        int limit = request.limit() != null ? request.limit() : 20;
        producer.importBooks(request.query(), request.title(), request.author(), limit);
        return ResponseEntity.accepted().build();
    }
}
