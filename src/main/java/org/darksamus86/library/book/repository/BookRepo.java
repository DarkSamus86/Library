package org.darksamus86.library.book.repository;

import org.darksamus86.library.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepo extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);

    Optional<Book> findByIsbn(String isbn);

    java.util.List<Book> findByTitleContainingIgnoreCase(String title);

    long countByIsActive(boolean isActive);
}
