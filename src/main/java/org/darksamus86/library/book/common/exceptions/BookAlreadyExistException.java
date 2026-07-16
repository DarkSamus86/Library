package org.darksamus86.library.book.common.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BookAlreadyExistException extends RuntimeException {
    private final HttpStatus httpStatus = HttpStatus.CONFLICT;
    private final String errorCode = "BOOK_ALREADY_EXIST";

    public BookAlreadyExistException(String isbn) {
        super("Book with this isbn is already exist: " + isbn);
    }

}
