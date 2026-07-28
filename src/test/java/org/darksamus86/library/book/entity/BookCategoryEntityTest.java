package org.darksamus86.library.book.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookCategoryEntityTest {

    @Test
    @DisplayName("Should use surrogate id and keep book/category association")
    void builder_ShouldCreateAssociationWithSurrogateId() {
        Book book = Book.builder().id(1L).title("Book").build();
        Category category = Category.builder().id(2L).name("Programming").build();

        BookCategory link = BookCategory.builder()
                .id(10L)
                .book(book)
                .category(category)
                .build();

        assertThat(link.getId()).isEqualTo(10L);
        assertThat(link.getBook()).isSameAs(book);
        assertThat(link.getCategory()).isSameAs(category);
    }
}
