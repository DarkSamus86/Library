package org.darksamus86.library.book.dto.request;

public record BookImportRequest(
        String query,
        String title,
        String author,
        Integer limit
) {}
