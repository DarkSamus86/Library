package org.darksamus86.library.book.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book_genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookGenre {
    @EmbeddedId
    private BookGenreId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("bookId")
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("genreId")
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;
}
