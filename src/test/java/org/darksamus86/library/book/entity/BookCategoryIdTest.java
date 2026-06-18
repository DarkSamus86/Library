package org.darksamus86.library.book.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookCategoryIdTest {

    @Test
    @DisplayName("Should create with all args constructor")
    void allArgsConstructor_ShouldCreateId() {
        BookCategoryId id = new BookCategoryId(1L, 2L);

        assertThat(id.getBookId()).isEqualTo(1L);
        assertThat(id.getCategoryId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should be equal when same values")
    void equals_ShouldReturnTrueForSameValues() {
        BookCategoryId id1 = new BookCategoryId(1L, 2L);
        BookCategoryId id2 = new BookCategoryId(1L, 2L);

        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when different values")
    void equals_ShouldReturnFalseForDifferentValues() {
        BookCategoryId id1 = new BookCategoryId(1L, 2L);
        BookCategoryId id2 = new BookCategoryId(1L, 3L);

        assertThat(id1).isNotEqualTo(id2);
    }
}
