package org.darksamus86.library.book.repository;

import org.darksamus86.library.book.entity.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCategoryRepo extends JpaRepository<BookCategory, Long> {
}
