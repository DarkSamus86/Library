package org.darksamus86.library.book.repository;

import org.darksamus86.library.book.entity.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookCategoryRepo extends JpaRepository<BookCategory, Long> {
    @Query("SELECT bc.book.id FROM BookCategory bc WHERE bc.category.id = :categoryId")
    List<Long> findBookIdsByCategoryId(Long categoryId);
}
