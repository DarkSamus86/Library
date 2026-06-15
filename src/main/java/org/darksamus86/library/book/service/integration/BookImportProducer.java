package org.darksamus86.library.book.service.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.darksamus86.library.book.dto.integration.OpenLibraryBook;
import org.darksamus86.library.book.dto.integration.OpenLibrarySearchResponse;
import org.darksamus86.library.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookImportProducer {

    private final RabbitTemplate rabbitTemplate;
    private final RestTemplate restTemplate;

    private static final String OPEN_LIBRARY_SEARCH = "https://openlibrary.org/search.json?q={query}&limit=20&sort=new";

    @Scheduled(cron = "0 0 3 * * ?")
    public void scheduledImport() {
        log.info("Starting scheduled book import from Open Library");
        importByQuery("java");
    }

    public void importByQuery(String query) {
        log.info("Importing books for query: {}", query);

        try {
            var response = restTemplate.getForObject(
                    OPEN_LIBRARY_SEARCH,
                    OpenLibrarySearchResponse.class,
                    query
            );

            if (response == null || response.docs() == null || response.docs().isEmpty()) {
                log.warn("No books found for query: {}", query);
                return;
            }

            log.info("Found {} books for query: {}", response.docs().size(), query);

            for (OpenLibraryBook book : response.docs()) {
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.BOOK_EXCHANGE,
                        RabbitMQConfig.ROUTING_KEY_BOOK_IMPORT,
                        book
                );
            }

            log.info("Sent {} books to queue for query: {}", response.docs().size(), query);
        } catch (Exception e) {
            log.error("Failed to import books for query '{}'", query, e);
        }
    }
}
