package org.darksamus86.library.book.common.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BookNotFoundException extends RuntimeException {

  private final HttpStatus status = HttpStatus.NOT_FOUND;
  private final String errorCode = "BOOK_NOT_FOUND";

  public BookNotFoundException(Long id) {
    // Формируем понятное сообщение
    super("Книга с ID %d не найдена".formatted(id));
  }

  public BookNotFoundException(String message) {
    super(message);
  }
}
