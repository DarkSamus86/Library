package org.darksamus86.library.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.darksamus86.library.book.dto.request.BookImportRequest;
import org.darksamus86.library.book.service.integration.BookImportProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookImportControllerTest {

    @Mock
    private BookImportProducer producer;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        BookImportController controller = new BookImportController(producer);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void importBooks_WithQuery_ShouldReturnAccepted() throws Exception {
        BookImportRequest request = new BookImportRequest("George Orwell", null, null, null);

        mockMvc.perform(post("/api/v1/books/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());

        verify(producer, times(1)).importBooks("George Orwell", null, null, 20);
    }

    @Test
    void importBooks_WithTitleAndAuthor_ShouldReturnAccepted() throws Exception {
        BookImportRequest request = new BookImportRequest(null, "Animal Farm", "Orwell", 10);

        mockMvc.perform(post("/api/v1/books/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());

        verify(producer, times(1)).importBooks(null, "Animal Farm", "Orwell", 10);
    }

    @Test
    void importBooks_WithoutParams_ShouldReturnBadRequest() throws Exception {
        BookImportRequest request = new BookImportRequest(null, null, null, null);

        mockMvc.perform(post("/api/v1/books/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(producer);
    }

    @Test
    void importBooks_WithOnlyLimit_ShouldReturnBadRequest() throws Exception {
        BookImportRequest request = new BookImportRequest(null, null, null, 50);

        mockMvc.perform(post("/api/v1/books/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(producer);
    }

    @Test
    void importBooks_WithBlankQuery_ShouldReturnBadRequest() throws Exception {
        BookImportRequest request = new BookImportRequest("   ", null, null, null);

        mockMvc.perform(post("/api/v1/books/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(producer);
    }
}
