package org.darksamus86.library.book.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "isbn", unique = true)
    private String isbn;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "rental_price", precision = 10, scale = 2)
    private BigDecimal rentalPrice;

    @Column(name = "deposit_amount", precision = 10, scale = 2)
    private BigDecimal depositAmount;

    @Column(name = "stock_count", nullable = false)
    private Integer stockCount;

    @Column(name = "published_year")
    private Integer publishedYear;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BookAuthor> bookAuthors = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addAuthor(Author author, AuthorRole role, Integer order) {
        BookAuthor bookAuthor = BookAuthor.builder()
                .book(this)
                .author(author)
                .authorRole(role)
                .authorOrder(order)
                .build();

        bookAuthors.add(bookAuthor);
        author.getBookAuthors().add(bookAuthor);
    }

    public void removeAuthor(Author author) {
        bookAuthors.removeIf(link -> {
            boolean match = link.getAuthor().equals(author);
            if (match) {
                author.getBookAuthors().remove(link);
                link.setBook(null);
                link.setAuthor(null);
            }
            return match;
        });
    }
}