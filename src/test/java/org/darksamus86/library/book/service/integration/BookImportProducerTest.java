package org.darksamus86.library.book.service.integration;

import org.darksamus86.library.book.dto.integration.OpenLibraryBook;
import org.darksamus86.library.book.dto.integration.OpenLibrarySearchResponse;
import org.darksamus86.library.config.RabbitMQConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookImportProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BookImportProducer producer;

    private OpenLibraryBook createBook(String title, String isbn) {
        return new OpenLibraryBook("/works/1", title, List.of("Author"), List.of(isbn), 1, 2020, List.of("Publisher"));
    }

    @Test
    @DisplayName("Should import books and send to queue")
    void importBooks_ShouldSendToQueue() {
        OpenLibraryBook book1 = createBook("Book 1", "111");
        OpenLibraryBook book2 = createBook("Book 2", "222");
        OpenLibrarySearchResponse response = new OpenLibrarySearchResponse(2, List.of(book1, book2));

        when(restTemplate.getForObject(anyString(), eq(OpenLibrarySearchResponse.class))).thenReturn(response);

        producer.importBooks("java", null, null, 10);

        verify(rabbitTemplate, times(2)).convertAndSend(
                eq(RabbitMQConfig.BOOK_EXCHANGE),
                eq(RabbitMQConfig.ROUTING_KEY_BOOK_IMPORT),
                any(OpenLibraryBook.class)
        );
    }

    @Test
    @DisplayName("Should not send anything when no books found")
    void importBooks_WhenNoResults_ShouldNotSend() {
        OpenLibrarySearchResponse response = new OpenLibrarySearchResponse(0, List.of());

        when(restTemplate.getForObject(anyString(), eq(OpenLibrarySearchResponse.class))).thenReturn(response);

        producer.importBooks("nonexistent", null, null, 10);

        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    @DisplayName("Should not send anything when response is null")
    void importBooks_WhenNullResponse_ShouldNotSend() {
        when(restTemplate.getForObject(anyString(), eq(OpenLibrarySearchResponse.class))).thenReturn(null);

        producer.importBooks("query", null, null, 10);

        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    @DisplayName("Should not send anything when docs is null")
    void importBooks_WhenNullDocs_ShouldNotSend() {
        OpenLibrarySearchResponse response = new OpenLibrarySearchResponse(0, null);

        when(restTemplate.getForObject(anyString(), eq(OpenLibrarySearchResponse.class))).thenReturn(response);

        producer.importBooks("query", null, null, 10);

        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    @DisplayName("Should use default limit when null")
    void importBooks_WhenNullLimit_ShouldUseDefault() {
        OpenLibrarySearchResponse response = new OpenLibrarySearchResponse(0, List.of());
        when(restTemplate.getForObject(anyString(), eq(OpenLibrarySearchResponse.class))).thenReturn(response);

        producer.importBooks("query", null, null, null);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).getForObject(urlCaptor.capture(), eq(OpenLibrarySearchResponse.class));

        assertThat(urlCaptor.getValue()).contains("limit=20");
    }

    @Test
    @DisplayName("Should build URL with title parameter")
    void importBooks_WithTitle_ShouldIncludeInUrl() {
        OpenLibrarySearchResponse response = new OpenLibrarySearchResponse(0, List.of());
        when(restTemplate.getForObject(anyString(), eq(OpenLibrarySearchResponse.class))).thenReturn(response);

        producer.importBooks(null, "Java Programming", null, 5);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).getForObject(urlCaptor.capture(), eq(OpenLibrarySearchResponse.class));

        assertThat(urlCaptor.getValue()).contains("title=");
    }

    @Test
    @DisplayName("Should build URL with author parameter")
    void importBooks_WithAuthor_ShouldIncludeInUrl() {
        OpenLibrarySearchResponse response = new OpenLibrarySearchResponse(0, List.of());
        when(restTemplate.getForObject(anyString(), eq(OpenLibrarySearchResponse.class))).thenReturn(response);

        producer.importBooks(null, null, "Joshua Bloch", 5);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).getForObject(urlCaptor.capture(), eq(OpenLibrarySearchResponse.class));

        assertThat(urlCaptor.getValue()).contains("author=");
    }

    @Test
    @DisplayName("Should handle exception gracefully")
    void importBooks_WhenRestTemplateThrows_ShouldNotPropagate() {
        when(restTemplate.getForObject(anyString(), eq(OpenLibrarySearchResponse.class)))
                .thenThrow(new RuntimeException("API Error"));

        producer.importBooks("query", null, null, 10);

        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    @DisplayName("Should import by query")
    void importByQuery_ShouldCallImportBooks() {
        OpenLibrarySearchResponse response = new OpenLibrarySearchResponse(0, List.of());
        when(restTemplate.getForObject(anyString(), eq(OpenLibrarySearchResponse.class))).thenReturn(response);

        producer.importByQuery("spring boot");

        verify(restTemplate).getForObject(anyString(), eq(OpenLibrarySearchResponse.class));
    }
}
