package org.darksamus86.library.book.service;

import org.darksamus86.library.book.common.exceptions.BookNotFoundException;
import org.darksamus86.library.book.dto.request.BookPricesRequest;
import org.darksamus86.library.book.dto.request.CreateBookRequest;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepo bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    @Test
    @DisplayName("Должен вернуть книгу, если она существует")
    void getBookById_WhenBookExists_ShouldReturnDto() {
        Long bookId = 1L;
        Book bookEntity = new Book();
        bookEntity.setId(bookId);
        bookEntity.setTitle("Test Book");

        ResponseGetBook expectedDto = new ResponseGetBook(1L, "Test Book", null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, true, true, 0, -1, true, true, 0, null, 0, 0);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookEntity));
        when(bookMapper.toResponse(bookEntity)).thenReturn(expectedDto);

        ResponseGetBook result = bookService.getBookById(bookId);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Test Book");

        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    @DisplayName("Должен выбросить ошибку, если книги нет")
    void getBookById_WhenBookNotFound_ShouldThrowException() {
        Long bookId = 999L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById(bookId))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("999");

        verify(bookRepository).findById(bookId);
    }

    @Test
    @DisplayName("Должен создать книгу")
    void createBook_ValidRequest_ShouldReturnSavedBook() {
        CreateBookRequest request = new CreateBookRequest("New Book", "Desc", "123",
                new BigDecimal("10"), null, null, 5, -1, true, true, 2023, "url");

        Book newBook = new Book();
        newBook.setId(1L);
        newBook.setTitle("New Book");

        ResponseGetBook responseDto = new ResponseGetBook(1L, "New Book", "Desc",
                new BigDecimal("10"), null, null, true, true, 5, -1, true, true, 2023, null, 0, 0);

        when(bookRepository.existsByIsbn("123")).thenReturn(false);
        when(bookMapper.toEntity(request)).thenReturn(newBook);
        when(bookRepository.save(any(Book.class))).thenReturn(newBook);
        when(bookMapper.toResponse(newBook)).thenReturn(responseDto);

        ResponseGetBook result = bookService.createBook(request);

        assertThat(result).isNotNull();
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("Должен очистить ISBN от дефисов и пробелов при создании книги")
    void createBook_WithHyphenatedIsbn_ShouldSanitizeIsbn() {
        CreateBookRequest request = new CreateBookRequest("New Book", "Desc", " 978-0-13-468599-1 ",
                new BigDecimal("10"), null, null, 5, -1, true, true, 2023, "url");

        Book bookEntity = new Book();
        bookEntity.setId(1L);
        bookEntity.setTitle("New Book");
        bookEntity.setIsbn("9780134685991");

        ResponseGetBook responseDto = new ResponseGetBook(1L, "New Book", "Desc",
                new BigDecimal("10"), null, null, true, true, 5, -1, true, true, 2023, null, 0, 0);

        when(bookRepository.existsByIsbn("9780134685991")).thenReturn(false);
        when(bookMapper.toEntity(request)).thenReturn(bookEntity);
        when(bookRepository.save(any(Book.class))).thenReturn(bookEntity);
        when(bookMapper.toResponse(bookEntity)).thenReturn(responseDto);

        bookService.createBook(request);

        verify(bookRepository).existsByIsbn("9780134685991");
        verify(bookRepository).save(bookEntity);
        assertThat(bookEntity.getIsbn()).isEqualTo("9780134685991");
    }

    @Test
    @DisplayName("Должен успешно установить цены для книги")
    void updateBookPrices_ShouldUpdatePricesAndReturnResponse() {
        Long bookId = 1L;
        BookPricesRequest request = new BookPricesRequest(
                new BigDecimal("299.99"), new BigDecimal("49.99"), new BigDecimal("100.00")
        );

        Book existingBook = new Book();
        existingBook.setId(bookId);
        existingBook.setTitle("Existing Book");
        existingBook.setPricePurchase(BigDecimal.ZERO);

        ResponseGetBook responseDto = new ResponseGetBook(bookId, "Existing Book", "Desc",
                new BigDecimal("299.99"), new BigDecimal("49.99"), new BigDecimal("100.00"), true, true, 5, -1, true, true, 2023, null, 0, 0);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(existingBook);
        when(bookMapper.toResponse(existingBook)).thenReturn(responseDto);

        ResponseGetBook result = bookService.updateBookPrices(bookId, request);

        assertThat(result).isNotNull();
        assertThat(existingBook.getPricePurchase()).isEqualTo(new BigDecimal("299.99"));
        assertThat(existingBook.getPriceRental()).isEqualTo(new BigDecimal("49.99"));
        assertThat(existingBook.getDepositAmount()).isEqualTo(new BigDecimal("100.00"));
        verify(bookRepository).save(existingBook);
    }
}
