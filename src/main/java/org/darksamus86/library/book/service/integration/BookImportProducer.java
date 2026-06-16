package org.darksamus86.library.book.service.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.darksamus86.library.book.dto.integration.OpenLibraryBook;
import org.darksamus86.library.book.dto.integration.OpenLibrarySearchResponse;
import org.darksamus86.library.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookImportProducer {

    private final RabbitTemplate rabbitTemplate;
    private final RestTemplate restTemplate;

    @Scheduled(cron = "0 0 3 * * ?")
    public void scheduledImport() {
        log.info("Starting scheduled book import from Open Library");
        importBooks("java", null, null, 20);
    }

    public void importByQuery(String query) {
        importBooks(query, null, null, 20);
    }

    public void importBooks(String query, String title, String author, Integer limit) {
        log.info("Importing books from Open Library - query: {}, title: {}, author: {}, limit: {}", query, title, author, limit);

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("https://openlibrary.org/search.json");
            
            if (limit != null) {
                builder.queryParam("limit", limit);
            } else {
                builder.queryParam("limit", 20);
            }
            
            builder.queryParam("sort", "new");

            if (query != null && !query.isBlank()) {
                builder.queryParam("q", query);
            }
            if (title != null && !title.isBlank()) {
                builder.queryParam("title", title);
            }
            if (author != null && !author.isBlank()) {
                builder.queryParam("author", author);
            }

            String url = builder.toUriString();
            log.debug("Built Open Library URL: {}", url);

            var response = restTemplate.getForObject(
                    url,
                    OpenLibrarySearchResponse.class
            );

            if (response == null || response.docs() == null || response.docs().isEmpty()) {
                log.warn("No books found for parameters - query: {}, title: {}, author: {}", query, title, author);
                return;
            }

            log.info("Found {} books from Open Library", response.docs().size());

            for (OpenLibraryBook book : response.docs()) {
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.BOOK_EXCHANGE,
                        RabbitMQConfig.ROUTING_KEY_BOOK_IMPORT,
                        book
                );
            }

            log.info("Sent {} books to queue for processing", response.docs().size());
        } catch (Exception e) {
            log.error("Failed to import books from Open Library", e);
        }
    }
}
