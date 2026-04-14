package org.darksamus86.library.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.darksamus86.library.book.dto.request.CreateBookRequest;
import org.darksamus86.library.book.dto.request.UpdateBookRequest;
import org.darksamus86.library.book.dto.response.ResponseGetBook;
import org.darksamus86.library.book.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    /**
     * Получить все книги
     * GET /api/v1/books?page=0&size=10&sort=title,asc
     */
    @GetMapping
    public ResponseEntity<Page<ResponseGetBook>> getAllBooks(
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("Getting all books with pagination");
        return ResponseEntity.ok(bookService.getAllBooks(pageable));
    }

    /**
     * Получить все книги без пагинации
     * GET /api/v1/books/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<ResponseGetBook>> getAllBooksList() {
        log.info("Getting all books without pagination");
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    /**
     * Получить книгу по ID
     * GET /api/v1/books/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseGetBook> getBookById(@PathVariable Long id) {
        log.info("Getting book by id: {}", id);
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    /**
     * Создать новую книгу
     * POST /api/v1/books
     */
    @PostMapping
    public ResponseEntity<ResponseGetBook> createBook(
            @Valid @RequestBody CreateBookRequest request) {
        log.info("Creating new book: {}", request.title());
        ResponseGetBook createdBook = bookService.createBook(request);

        return ResponseEntity
                .created(URI.create("/api/v1/books/" + createdBook.hashCode())) // Лучше использовать реальный ID
                .body(createdBook);
    }

    /**
     * Обновить книгу
     * PUT /api/v1/books/1
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResponseGetBook> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookRequest request) {
        log.info("Updating book with id: {}", id);
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    /**
     * Частичное обновление книги
     * PATCH /api/v1/books/1
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseGetBook> partialUpdateBook(
            @PathVariable Long id,
            @RequestBody UpdateBookRequest request) {
        log.info("Partially updating book with id: {}", id);
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    /**
     * Удалить книгу (мягкое удаление)
     * DELETE /api/v1/books/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        log.info("Deleting book with id: {}", id);
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Поиск книг по названию
     * GET /api/v1/books/search?title=java
     */
    @GetMapping("/search")
    public ResponseEntity<List<ResponseGetBook>> searchBooks(
            @RequestParam String title) {
        log.info("Searching books by title: {}", title);
        return ResponseEntity.ok(bookService.searchBooksByTitle(title));
    }
}
