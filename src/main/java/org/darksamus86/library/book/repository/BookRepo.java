package org.darksamus86.library.book.repository;

import org.darksamus86.library.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepo extends JpaRepository<Book, Long> {
}
