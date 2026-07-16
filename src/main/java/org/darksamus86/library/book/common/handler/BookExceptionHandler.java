package org.darksamus86.library.book.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.darksamus86.library.book.common.exceptions.BookAlreadyExistException;
import org.darksamus86.library.book.common.exceptions.BookNotFoundException;
import org.darksamus86.library.book.common.exceptions.IsbnAlreadyExist;
import org.darksamus86.library.config.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

// Ограничиваем область действия ТОЛЬКО пакетом book
@RestControllerAdvice(basePackages = "org.darksamus86.library.book")
@Slf4j
public class BookExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<?> handleBookNotFound(BookNotFoundException e) {
        log.warn("Book not found: {}", e.getMessage());
        return ResponseEntity.status(404).body(new ErrorResponse(404, e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler
    public ResponseEntity<?> handleBookAlreadyExist(BookAlreadyExistException e) {
        log.warn("Book already exist: {}", e.getMessage());
        return ResponseEntity.status(409).body(new ErrorResponse(409, e.getMessage(), LocalDateTime.now()));
    }

    public ResponseEntity<?> handleIsbnAlreadyExist(IsbnAlreadyExist e) {
        log.warn("");
        return ResponseEntity.status(409).body(new ErrorResponse(409, e.getMessage(), LocalDateTime.now()));
    }
}