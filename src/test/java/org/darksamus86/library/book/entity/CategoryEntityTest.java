package org.darksamus86.library.book.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryEntityTest {

    @Test
    @DisplayName("Should build category with builder")
    void builder_ShouldCreateCategory() {
        Category category = Category.builder()
                .id(1L)
                .name("Programming")
                .build();

        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getName()).isEqualTo("Programming");
    }

    @Test
    @DisplayName("Should set createdAt on PrePersist")
    void onCreate_ShouldSetTimestamp() {
        Category category = new Category();
        category.setName("Science");

        category.onCreate();

        assertThat(category.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should initialize bookCategories with empty list by default")
    void bookCategories_ShouldBeInitializedByDefault() {
        Category category = new Category();
        assertThat(category.getBookCategories()).isNotNull();
        assertThat(category.getBookCategories()).isEmpty();
    }
}
