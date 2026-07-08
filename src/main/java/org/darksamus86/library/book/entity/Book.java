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

    @Column(name = "price_purchase", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePurchase;

    @Column(name = "price_rental", precision = 10, scale = 2)
    private BigDecimal priceRental;

    @Column(name = "deposit_amount", precision = 10, scale = 2)
    private BigDecimal depositAmount;

    @Column(name = "has_physical", nullable = false)
    private Boolean hasPhysical;

    @Column(name = "has_digital", nullable = false)
    private Boolean hasDigital;

    @Column(name = "physical_inventory", nullable = false)
    private Integer physicalInventory;

    @Column(name = "digital_licenses", nullable = false)
    private Integer digitalLicenses;

    @Column(name = "is_available_for_rent", nullable = false)
    private Boolean isAvailableForRent;

    @Column(name = "is_available_for_purchase", nullable = false)
    private Boolean isAvailableForPurchase;

    @Column(name = "total_rentals_count", nullable = false)
    private Integer totalRentalsCount;

    @Column(name = "total_purchases_count", nullable = false)
    private Integer totalPurchasesCount;

    @Column(name = "published_year")
    private Integer publishedYear;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

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