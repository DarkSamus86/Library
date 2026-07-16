package org.darksamus86.library.book.common.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BookAlreadyExistException extends RuntimeException {
    private final HttpStatus httpStatus = HttpStatus.CONFLICT;
    private final String errorCode = "BOOK_ALREADY_EXIST";

    public BookAlreadyExistException(String message) {
        super(message);
    }

}
