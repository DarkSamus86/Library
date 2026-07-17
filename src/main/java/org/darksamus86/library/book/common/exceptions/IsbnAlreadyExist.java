package org.darksamus86.library.book.common.exceptions;

public class IsbnAlreadyExist extends BookAlreadyExistException {

    public IsbnAlreadyExist(String isbn) {
        super("Book with this isbn is already exist: " + isbn);
    }
}
