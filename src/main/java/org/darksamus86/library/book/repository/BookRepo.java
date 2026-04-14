package org.darksamus86.library.book.repository;

import org.darksamus86.library.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepo extends JpaRepository<Book, Long> {
    // Проверка существования по ISBN
    boolean existsByIsbn(String isbn);

    // Поиск по ISBN
    Optional<Book> findByIsbn(String isbn);

    // Поиск по названию (частичное совпадение)
    java.util.List<Book> findByTitleContainingIgnoreCase(String title);
}
