package org.darksamus86.library.book.dto.integration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenLibraryBook(
        String key,
        String title,
        List<String> author_name,
        List<String> isbn,
        Integer cover_i,
        Integer first_publish_year,
        List<String> publisher
) {
}
