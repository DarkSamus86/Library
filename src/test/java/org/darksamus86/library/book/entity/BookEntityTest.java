package org.darksamus86.library.book.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookEntityTest {

    @Test
    @DisplayName("Should set createdAt and updatedAt on PrePersist")
    void onCreate_ShouldSetTimestampsAndActiveFlag() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setPrice(BigDecimal.TEN);
        book.setStockCount(5);

        book.onCreate();

        assertThat(book.getCreatedAt()).isNotNull();
        assertThat(book.getUpdatedAt()).isNotNull();
        assertThat(book.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should not override isActive if already set")
    void onCreate_ShouldNotOverrideActiveFlag() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setPrice(BigDecimal.TEN);
        book.setStockCount(5);
        book.setIsActive(false);

        book.onCreate();

        assertThat(book.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("Should update updatedAt on PreUpdate")
    void onUpdate_ShouldUpdateTimestamp() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setPrice(BigDecimal.TEN);
        book.setStockCount(5);
        LocalDateTime before = LocalDateTime.now().minusHours(1);
        book.setUpdatedAt(before);

        book.onUpdate();

        assertThat(book.getUpdatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    @DisplayName("Should add author to book")
    void addAuthor_ShouldAddBookAuthorLink() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setPrice(BigDecimal.TEN);
        book.setStockCount(5);

        Author author = Author.builder().fullName("Test Author").build();

        book.addAuthor(author, AuthorRole.MAIN_AUTHOR, 1);

        assertThat(book.getBookAuthors()).hasSize(1);
        assertThat(author.getBookAuthors()).hasSize(1);

        BookAuthor link = book.getBookAuthors().get(0);
        assertThat(link.getBook()).isEqualTo(book);
        assertThat(link.getAuthor()).isEqualTo(author);
        assertThat(link.getAuthorRole()).isEqualTo(AuthorRole.MAIN_AUTHOR);
        assertThat(link.getAuthorOrder()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should remove author from book")
    void removeAuthor_ShouldRemoveBookAuthorLink() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setPrice(BigDecimal.TEN);
        book.setStockCount(5);

        Author author = Author.builder().fullName("Test Author").build();

        book.addAuthor(author, AuthorRole.MAIN_AUTHOR, 1);
        assertThat(book.getBookAuthors()).hasSize(1);

        book.removeAuthor(author);

        assertThat(book.getBookAuthors()).isEmpty();
        assertThat(author.getBookAuthors()).isEmpty();
    }

    @Test
    @DisplayName("Should build book with builder")
    void builder_ShouldCreateBook() {
        Book book = Book.builder()
                .title("Builder Book")
                .description("Description")
                .isbn("1234567890")
                .price(BigDecimal.TEN)
                .rentalPrice(BigDecimal.ONE)
                .depositAmount(BigDecimal.valueOf(5))
                .stockCount(10)
                .publishedYear(2023)
                .coverUrl("http://cover.url")
                .isActive(true)
                .build();

        assertThat(book.getTitle()).isEqualTo("Builder Book");
        assertThat(book.getDescription()).isEqualTo("Description");
        assertThat(book.getIsbn()).isEqualTo("1234567890");
        assertThat(book.getPrice()).isEqualTo(BigDecimal.TEN);
        assertThat(book.getRentalPrice()).isEqualTo(BigDecimal.ONE);
        assertThat(book.getDepositAmount()).isEqualTo(BigDecimal.valueOf(5));
        assertThat(book.getStockCount()).isEqualTo(10);
        assertThat(book.getPublishedYear()).isEqualTo(2023);
        assertThat(book.getCoverUrl()).isEqualTo("http://cover.url");
        assertThat(book.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should initialize bookAuthors with empty list by default")
    void bookAuthors_ShouldBeInitializedByDefault() {
        Book book = new Book();
        assertThat(book.getBookAuthors()).isNotNull();
        assertThat(book.getBookAuthors()).isEmpty();
    }
}
