package org.darksamus86.library.book.service;

import org.darksamus86.library.book.common.exceptions.BookNotFoundException;
import org.darksamus86.library.book.common.exceptions.IsbnAlreadyExist;
import org.darksamus86.library.book.dto.request.UpdateBookRequest;
import org.darksamus86.library.book.dto.response.ResponseGetBook;
import org.darksamus86.library.book.entity.Book;
import org.darksamus86.library.book.mapper.BookMapper;
import org.darksamus86.library.book.repository.BookRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceExtendedTest {

    @Mock
    private BookRepo bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    @Test
    @DisplayName("Should throw when creating book with duplicate ISBN")
    void createBook_WhenDuplicateIsbn_ShouldThrow() {
        var request = new org.darksamus86.library.book.dto.request.CreateBookRequest(
                "Book", "Desc", "123", "Test Author", "Fiction", null,
                BigDecimal.TEN, null, null, 5, -1, true, true, 2023, null);

        when(bookRepository.existsByIsbn("123")).thenReturn(true);

        assertThatThrownBy(() -> bookService.createBook(request))
                .isInstanceOf(IsbnAlreadyExist.class)
                .hasMessageContaining("123");
    }

    @Test
    @DisplayName("Should update book successfully")
    void updateBook_ShouldUpdateAndReturnResponse() {
        UpdateBookRequest request = new UpdateBookRequest(
                "New Title", "New Desc", null, null, null, null, null, -1, true, true, null, null, null, null, null);

        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle("Old Title");

        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setTitle("New Title");

        ResponseGetBook response = new ResponseGetBook(1L, "New Title", "New Desc",
                null, BigDecimal.TEN, null, null, true, true, 5, -1, true, true, 2023, null, 0, 0);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);
        when(bookMapper.toResponse(updatedBook)).thenReturn(response);

        ResponseGetBook result = bookService.updateBook(1L, request);

        assertThat(result.title()).isEqualTo("New Title");
        verify(bookRepository).save(existingBook);
    }

    @Test
    @DisplayName("Should throw when updating non-existent book")
    void updateBook_WhenNotFound_ShouldThrow() {
        UpdateBookRequest request = new UpdateBookRequest(
                "Title", null, null, null, null, null, null, -1, true, true, null, null, null, null, null);

        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBook(999L, request))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw when updating book with duplicate ISBN")
    void updateBook_WhenDuplicateIsbn_ShouldThrow() {
        UpdateBookRequest request = new UpdateBookRequest(
                null, null, "duplicateisbn", null, null, null, null, -1, true, true, null, null, null, null, null);

        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setIsbn("old-isbn");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.existsByIsbn("duplicateisbn")).thenReturn(true);

        assertThatThrownBy(() -> bookService.updateBook(1L, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should delete book (soft delete)")
    void deleteBook_ShouldDeactivate() {
        Book book = new Book();
        book.setId(1L);
        book.setIsActive(true);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        bookService.deleteBook(1L);

        assertThat(book.getIsActive()).isFalse();
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("Should throw when deleting non-existent book")
    void deleteBook_WhenNotFound_ShouldThrow() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.deleteBook(999L))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    @DisplayName("Should hard delete book")
    void hardDeleteBook_ShouldDeletePermanently() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        bookService.hardDeleteBook(1L);

        verify(bookRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw when hard deleting non-existent book")
    void hardDeleteBook_WhenNotFound_ShouldThrow() {
        when(bookRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> bookService.hardDeleteBook(999L))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    @DisplayName("Should search books by title")
    void searchBooksByTitle_ShouldReturnMatchingBooks() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Java Programming");
        book.setIsActive(true);

        ResponseGetBook response = new ResponseGetBook(1L, "Java Programming", null, null, null, null, null, true, true, 0, -1, true, true, null, null, 0, 0);

        when(bookRepository.findByTitleContainingIgnoreCase("java")).thenReturn(List.of(book));
        when(bookMapper.toResponse(book)).thenReturn(response);

        List<ResponseGetBook> result = bookService.searchBooksByTitle("java");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Java Programming");
    }

    @Test
    @DisplayName("Should filter inactive books when searching")
    void searchBooksByTitle_ShouldFilterInactiveBooks() {
        Book activeBook = new Book();
        activeBook.setId(1L);
        activeBook.setTitle("Active Book");
        activeBook.setIsActive(true);

        Book inactiveBook = new Book();
        inactiveBook.setId(2L);
        inactiveBook.setTitle("Inactive Book");
        inactiveBook.setIsActive(false);

        when(bookRepository.findByTitleContainingIgnoreCase("book")).thenReturn(List.of(activeBook, inactiveBook));
        when(bookMapper.toResponse(activeBook)).thenReturn(new ResponseGetBook(1L, "Active Book", null, null, null, null, null, true, true, 0, -1, true, true, null, null, 0, 0));

        List<ResponseGetBook> result = bookService.searchBooksByTitle("book");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Active Book");
    }

    @Test
    @DisplayName("Should get all books with pagination")
    void getAllBooks_WithPagination_ShouldReturnPage() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book 1");

        ResponseGetBook response = new ResponseGetBook(1L, "Book 1", null, null, null, null, null, true, true, 0, -1, true, true, null, null, 0, 0);
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<>(List.of(book), pageable, 1);

        when(bookRepository.findAll(pageable)).thenReturn(page);
        when(bookMapper.toResponse(book)).thenReturn(response);

        Page<ResponseGetBook> result = bookService.getAllBooks(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("Book 1");
    }

    @Test
    @DisplayName("Should get all books without pagination (only active)")
    void getAllBooks_WithoutPagination_ShouldReturnOnlyActive() {
        Book activeBook = new Book();
        activeBook.setId(1L);
        activeBook.setTitle("Active");
        activeBook.setIsActive(true);

        Book inactiveBook = new Book();
        inactiveBook.setId(2L);
        inactiveBook.setTitle("Inactive");
        inactiveBook.setIsActive(false);

        when(bookRepository.findAll()).thenReturn(List.of(activeBook, inactiveBook));
        when(bookMapper.toResponse(activeBook)).thenReturn(new ResponseGetBook(1L, "Active", null, null, null, null, null, true, true, 0, -1, true, true, null, null, 0, 0));

        List<ResponseGetBook> result = bookService.getAllBooks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Active");
    }

    @Test
    @DisplayName("Should throw when getting inactive book by id")
    void getBookById_WhenInactive_ShouldThrow() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Inactive Book");
        book.setIsActive(false);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookService.getBookById(1L))
                .isInstanceOf(BookNotFoundException.class);
    }
}
