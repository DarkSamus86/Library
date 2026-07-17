package org.darksamus86.library.book.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book_genres", uniqueConstraints = {
        @UniqueConstraint(name = "uk_book_genres_book_genre", columnNames = {"book_id", "genre_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;
}
