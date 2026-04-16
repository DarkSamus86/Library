package org.darksamus86.library.book.common.handler; // Или другой пакет для общей логики

import lombok.extern.slf4j.Slf4j;
import org.darksamus86.library.book.common.exceptions.BookNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice // Комбинация @ControllerAdvice + @ResponseBody
@Slf4j
public class BookExceptionHandler {

    // Этот метод перехватывает ТОЛЬКО наши бизнес-исключения
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleBookNotFound(BookNotFoundException ex) {
        log.warn("Book not found: {}", ex.getMessage());

        // Явно указываем тип Map<String, Object>
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", ex.getStatus().value(),
                "error", "Not Found",
                "code", ex.getErrorCode(),
                "message", ex.getMessage()
        );

        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    // Обработчик для всех остальных непредвиденных ошибок
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        // ВАРИАНТ 1: Явно указываем тип
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 500,
                "error", "Internal Server Error",
                "message", "Произошла внутренняя ошибка сервера"
        );

        return ResponseEntity.internalServerError().body(body);
    }
}