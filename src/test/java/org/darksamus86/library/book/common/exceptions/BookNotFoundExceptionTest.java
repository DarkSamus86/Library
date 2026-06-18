package org.darksamus86.library.book.common.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class BookNotFoundExceptionTest {

    @Test
    @DisplayName("Should create with Long id")
    void constructor_WithId_ShouldSetMessage() {
        BookNotFoundException ex = new BookNotFoundException(42L);

        assertThat(ex.getMessage()).isEqualTo("Книга с ID 42 не найдена");
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ex.getErrorCode()).isEqualTo("BOOK_NOT_FOUND");
    }

    @Test
    @DisplayName("Should create with custom message")
    void constructor_WithMessage_ShouldSetMessage() {
        BookNotFoundException ex = new BookNotFoundException("Custom message");

        assertThat(ex.getMessage()).isEqualTo("Custom message");
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ex.getErrorCode()).isEqualTo("BOOK_NOT_FOUND");
    }
}
