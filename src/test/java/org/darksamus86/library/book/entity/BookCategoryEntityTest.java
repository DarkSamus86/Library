package org.darksamus86.library.book.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookCategoryEntityTest {

    @Test
    @DisplayName("Should create book-category relation with surrogate id")
    void builder_ShouldCreateRelation() {
        Book book = Book.builder().id(1L).title("Book").build();
        Category category = Category.builder().id(2L).name("Category").build();

        BookCategory relation = BookCategory.builder()
                .id(10L)
                .book(book)
                .category(category)
                .build();

        assertThat(relation.getId()).isEqualTo(10L);
        assertThat(relation.getBook()).isSameAs(book);
        assertThat(relation.getCategory()).isSameAs(category);
    }
}
