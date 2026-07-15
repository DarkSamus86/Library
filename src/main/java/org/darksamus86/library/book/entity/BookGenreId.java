package org.darksamus86.library.book.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BookGenreId {
    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "genre_id")
    private Long genreId;
}
