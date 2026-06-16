package org.darksamus86.library.book.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.darksamus86.library.book.dto.request.BookPricesRequest;
import org.darksamus86.library.book.dto.request.CreateBookRequest;
import org.darksamus86.library.book.dto.request.UpdateBookRequest;
import org.darksamus86.library.book.dto.response.ResponseGetBook;
import org.darksamus86.library.book.entity.Book;
import org.darksamus86.library.book.common.exceptions.BookNotFoundException;
import org.darksamus86.library.book.mapper.BookMapper;
import org.darksamus86.library.book.repository.BookRepo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {
    private final BookRepo bookRepository;
    private final BookMapper bookMapper;

    /**
     * Получить книгу по ID
     */
    @Cacheable(value = "books", key = "#id")
    public ResponseGetBook getBookById(Long id) {
        log.debug("Fetching book by id: {}", id);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        if (Boolean.FALSE.equals(book.getIsActive())) {
            throw new BookNotFoundException(id);
        }

        return bookMapper.toResponse(book);
    }

    /**
     * Получить все книги (с пагинацией)
     */
    public Page<ResponseGetBook> getAllBooks(Pageable pageable) {
        log.debug("Fetching all books with pagination: {}", pageable);

        return bookRepository.findAll(pageable)
                .map(bookMapper::toResponse);
    }

    /**
     * Получить все книги (без пагинации)
     */
    public List<ResponseGetBook> getAllBooks() {
        log.debug("Fetching all books");

        return bookRepository.findAll().stream()
                .filter(Book::getIsActive) // Только активные
                .map(bookMapper::toResponse)
                .toList();
    }

    /**
     * Создать новую книгу
     */
    @Transactional
    @CacheEvict(value = "books", allEntries = true)
    public ResponseGetBook createBook(CreateBookRequest request) {
        log.info("Creating new book: {}", request.title());

        String isbn = request.isbn();
        if (isbn != null) {
            isbn = isbn.replace("-", "").replace(" ", "").trim();
        }

        // Проверка на дубликат ISBN
        if (isbn != null && bookRepository.existsByIsbn(isbn)) {
            throw new IllegalArgumentException("Книга с таким ISBN уже существует");
        }

        Book book = bookMapper.toEntity(request);
        book.setIsbn(isbn); // Сохраняем очищенный ISBN
        Book savedBook = bookRepository.save(book);

        log.info("Book created successfully with id: {}", savedBook.getId());
        return bookMapper.toResponse(savedBook);
    }

    /**
     * Обновить книгу
     */
    @Transactional
    @CacheEvict(value = "books", key = "#id", allEntries = true)
    public ResponseGetBook updateBook(Long id, UpdateBookRequest request) {
        log.info("Updating book with id: {}", id);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        String isbn = request.isbn();
        if (isbn != null) {
            isbn = isbn.replace("-", "").replace(" ", "").trim();
        }

        // Проверка на уникальность ISBN (если он меняется)
        if (isbn != null && !isbn.equals(book.getIsbn())) {
            if (bookRepository.existsByIsbn(isbn)) {
                throw new IllegalArgumentException("Книга с таким ISBN уже существует");
            }
        }

        bookMapper.updateEntityFromRequest(request, book);
        if (request.isbn() != null) {
            book.setIsbn(isbn); // Сохраняем очищенный ISBN
        }
        Book updatedBook = bookRepository.save(book);

        log.info("Book updated successfully with id: {}", updatedBook.getId());
        return bookMapper.toResponse(updatedBook);
    }

    /**
     * Установить цены для книги
     */
    @Transactional
    @CacheEvict(value = "books", key = "#id", allEntries = true)
    public ResponseGetBook updateBookPrices(Long id, BookPricesRequest request) {
        log.info("Updating prices for book with id: {}", id);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        book.setPrice(request.price());
        if (request.rentalPrice() != null) {
            book.setRentalPrice(request.rentalPrice());
        }
        if (request.depositAmount() != null) {
            book.setDepositAmount(request.depositAmount());
        }

        Book updatedBook = bookRepository.save(book);
        log.info("Book prices updated successfully for id: {}", updatedBook.getId());
        return bookMapper.toResponse(updatedBook);
    }

    /**
     * Удалить книгу (мягкое удаление - деактивация)
     */
    @Transactional
    @CacheEvict(value = "books", key = "#id", allEntries = true)
    public void deleteBook(Long id) {
        log.info("Deleting book with id: {}", id);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        // Мягкое удаление (деактивация)
        book.setIsActive(false);
        bookRepository.save(book);

        log.info("Book deactivated successfully with id: {}", id);
    }

    /**
     * Полное удаление книги
     */
    @Transactional
    @CacheEvict(value = "books", key = "#id", allEntries = true)
    public void hardDeleteBook(Long id) {
        log.warn("Hard deleting book with id: {}", id);

        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }

        bookRepository.deleteById(id);
        log.info("Book hard deleted successfully with id: {}", id);
    }

    /**
     * Поиск книг по названию
     */
    public List<ResponseGetBook> searchBooksByTitle(String title) {
        log.debug("Searching books by title: {}", title);

        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .filter(Book::getIsActive)
                .map(bookMapper::toResponse)
                .toList();
    }
}
