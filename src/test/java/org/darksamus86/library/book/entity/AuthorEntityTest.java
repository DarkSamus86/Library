package org.darksamus86.library.book.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorEntityTest {

    @Test
    @DisplayName("Should build author with builder")
    void builder_ShouldCreateAuthor() {
        Author author = Author.builder()
                .id(1L)
                .fullName("Joshua Bloch")
                .bio("Java expert")
                .build();

        assertThat(author.getId()).isEqualTo(1L);
        assertThat(author.getFullName()).isEqualTo("Joshua Bloch");
        assertThat(author.getBio()).isEqualTo("Java expert");
    }

    @Test
    @DisplayName("Should set createdAt on PrePersist")
    void onCreate_ShouldSetTimestamp() {
        Author author = new Author();
        author.setFullName("Test Author");

        author.onCreate();

        assertThat(author.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should initialize bookAuthors with empty list by default")
    void bookAuthors_ShouldBeInitializedByDefault() {
        Author author = new Author();
        assertThat(author.getBookAuthors()).isNotNull();
        assertThat(author.getBookAuthors()).isEmpty();
    }
}
