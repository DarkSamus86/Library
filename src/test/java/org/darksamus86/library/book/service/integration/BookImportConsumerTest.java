package org.darksamus86.library.book.service.integration;

import org.darksamus86.library.book.dto.integration.OpenLibraryBook;
import org.darksamus86.library.book.dto.integration.OpenLibrarySearchResponse;
import org.darksamus86.library.book.entity.Book;
import org.darksamus86.library.book.repository.BookRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookImportConsumerTest {

    @Mock
    private BookRepo bookRepo;

    @InjectMocks
    private BookImportConsumer consumer;

    private OpenLibraryBook createBook(String title, String isbn, String authorName, Integer year, Integer coverId) {
        return new OpenLibraryBook(
                "/works/1", title, List.of(authorName), List.of(isbn), coverId, year, List.of("Publisher")
        );
    }

    @Test
    @DisplayName("Should import book successfully")
    void handleBookImport_ShouldSaveBook() {
        OpenLibraryBook bookData = createBook("Test Book", "1234567890", "Author One", 2023, 12345);

        when(bookRepo.findByIsbn("1234567890")).thenReturn(Optional.empty());
        when(bookRepo.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));

        consumer.handleBookImport(bookData);

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepo).save(captor.capture());

        Book savedBook = captor.getValue();
        assertThat(savedBook.getTitle()).isEqualTo("Test Book");
        assertThat(savedBook.getIsbn()).isEqualTo("1234567890");
        assertThat(savedBook.getPublishedYear()).isEqualTo(2023);
        assertThat(savedBook.getPrice()).isEqualTo(BigDecimal.ZERO);
        assertThat(savedBook.getRentalPrice()).isEqualTo(BigDecimal.ZERO);
        assertThat(savedBook.getDepositAmount()).isEqualTo(BigDecimal.ZERO);
        assertThat(savedBook.getStockCount()).isEqualTo(1);
        assertThat(savedBook.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should skip book when ISBN already exists")
    void handleBookImport_WhenIsbnExists_ShouldSkip() {
        OpenLibraryBook bookData = createBook("Existing Book", "1234567890", "Author", 2023, 12345);

        when(bookRepo.findByIsbn("1234567890")).thenReturn(Optional.of(new Book()));

        consumer.handleBookImport(bookData);

        verify(bookRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should import book without ISBN")
    void handleBookImport_WhenNoIsbn_ShouldSaveBook() {
        OpenLibraryBook bookData = new OpenLibraryBook(
                "/works/2", "Book Without ISBN", List.of("Author"), null, 67890, 2020, List.of("Publisher")
        );

        when(bookRepo.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));

        consumer.handleBookImport(bookData);

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepo).save(captor.capture());

        Book savedBook = captor.getValue();
        assertThat(savedBook.getTitle()).isEqualTo("Book Without ISBN");
        assertThat(savedBook.getIsbn()).isNull();
    }

    @Test
    @DisplayName("Should import book with empty ISBN list")
    void handleBookImport_WhenEmptyIsbnList_ShouldSaveBook() {
        OpenLibraryBook bookData = new OpenLibraryBook(
                "/works/3", "Book Empty ISBN", List.of("Author"), List.of(), 11111, 2021, List.of("Publisher")
        );

        when(bookRepo.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));

        consumer.handleBookImport(bookData);

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepo).save(captor.capture());

        Book savedBook = captor.getValue();
        assertThat(savedBook.getTitle()).isEqualTo("Book Empty ISBN");
        assertThat(savedBook.getIsbn()).isNull();
    }

    @Test
    @DisplayName("Should sanitize ISBN from hyphens and spaces")
    void handleBookImport_ShouldSanitizeIsbn() {
        OpenLibraryBook bookData = createBook("Test Book", " 978-0-13-468599-1 ", "Author", 2023, 12345);

        when(bookRepo.findByIsbn("9780134685991")).thenReturn(Optional.empty());
        when(bookRepo.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));

        consumer.handleBookImport(bookData);

        verify(bookRepo).findByIsbn("9780134685991");

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepo).save(captor.capture());

        assertThat(captor.getValue().getIsbn()).isEqualTo("9780134685991");
    }

    @Test
    @DisplayName("Should generate cover URL from cover_i")
    void handleBookImport_ShouldGenerateCoverUrl() {
        OpenLibraryBook bookData = createBook("Test Book", "1234567890", "Author", 2023, 99999);

        when(bookRepo.findByIsbn("1234567890")).thenReturn(Optional.empty());
        when(bookRepo.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));

        consumer.handleBookImport(bookData);

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepo).save(captor.capture());

        assertThat(captor.getValue().getCoverUrl()).isEqualTo("https://covers.openlibrary.org/b/id/99999-L.jpg");
    }

    @Test
    @DisplayName("Should set null cover URL when cover_i is null")
    void handleBookImport_WhenNoCoverId_ShouldSetNullCoverUrl() {
        OpenLibraryBook bookData = createBook("Test Book", "1234567890", "Author", 2023, null);

        when(bookRepo.findByIsbn("1234567890")).thenReturn(Optional.empty());
        when(bookRepo.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));

        consumer.handleBookImport(bookData);

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepo).save(captor.capture());

        assertThat(captor.getValue().getCoverUrl()).isNull();
    }
}
