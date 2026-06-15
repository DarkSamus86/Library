package org.darksamus86.library.book.dto.integration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenLibrarySearchResponse(
        int numFound,
        List<OpenLibraryBook> docs
) {
}
