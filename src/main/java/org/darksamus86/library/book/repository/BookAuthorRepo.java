package org.darksamus86.library.book.repository;

import org.darksamus86.library.book.entity.BookAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookAuthorRepo extends JpaRepository<BookAuthor, Long> {
}
