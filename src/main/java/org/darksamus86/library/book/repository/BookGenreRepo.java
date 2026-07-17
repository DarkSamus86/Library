package org.darksamus86.library.book.repository;

import org.darksamus86.library.book.entity.BookGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookGenreRepo extends JpaRepository<BookGenre, Long> {
    @Query("SELECT bg.book.id FROM BookGenre bg WHERE bg.genre.id = :genreId")
    List<Long> findBookIdsByGenreId(@Param("genreId") Long genreId);
}
