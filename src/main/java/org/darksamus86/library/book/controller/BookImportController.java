package org.darksamus86.library.book.controller;

import lombok.RequiredArgsConstructor;
import org.darksamus86.library.book.service.integration.BookImportProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books/import")
@RequiredArgsConstructor
public class BookImportController {

    private final BookImportProducer producer;

    @PostMapping
    public ResponseEntity<Void> importBooks(@RequestParam String query) {
        producer.importByQuery(query);
        return ResponseEntity.accepted().build();
    }
}
