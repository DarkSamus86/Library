package org.darksamus86.library.book.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookAuthorEntityTest {

    @Test
    @DisplayName("Should build BookAuthor with builder")
    void builder_ShouldCreateBookAuthor() {
        Book book = new Book();
        book.setId(1L);
        Author author = new Author();
        author.setId(2L);

        BookAuthor bookAuthor = BookAuthor.builder()
                .id(10L)
                .book(book)
                .author(author)
                .authorRole(AuthorRole.MAIN_AUTHOR)
                .authorOrder(1)
                .build();

        assertThat(bookAuthor.getId()).isEqualTo(10L);
        assertThat(bookAuthor.getBook()).isEqualTo(book);
        assertThat(bookAuthor.getAuthor()).isEqualTo(author);
        assertThat(bookAuthor.getAuthorRole()).isEqualTo(AuthorRole.MAIN_AUTHOR);
        assertThat(bookAuthor.getAuthorOrder()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should set createdAt on PrePersist")
    void onCreate_ShouldSetTimestamp() {
        BookAuthor bookAuthor = new BookAuthor();

        bookAuthor.onCreate();

        assertThat(bookAuthor.getCreatedAt()).isNotNull();
    }
}
