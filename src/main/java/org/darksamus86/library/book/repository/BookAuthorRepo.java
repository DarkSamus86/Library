package org.darksamus86.library.book.repository;

import org.darksamus86.library.book.entity.BookAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookAuthorRepo extends JpaRepository<BookAuthor, Long> {
    @Query("SELECT ba.book.id FROM BookAuthor ba WHERE ba.author.id = :authorId")
    List<Long> findBookIdsByAuthorId(Long authorId);
}
