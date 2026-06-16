package org.darksamus86.library.book.controller;

import org.darksamus86.library.book.service.integration.BookImportProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    @BeforeEach
    void setUp() {
        BookImportController controller = new BookImportController(producer);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void importBooks_WithQuery_ShouldReturnAccepted() throws Exception {
        mockMvc.perform(post("/api/v1/books/import")
                        .param("query", "George Orwell"))
                .andExpect(status().isAccepted());

        verify(producer, times(1)).importBooks("George Orwell", null, null, 20);
    }

    @Test
    void importBooks_WithTitleAndAuthor_ShouldReturnAccepted() throws Exception {
        mockMvc.perform(post("/api/v1/books/import")
                        .param("title", "Animal Farm")
                        .param("author", "Orwell")
                        .param("limit", "10"))
                .andExpect(status().isAccepted());

        verify(producer, times(1)).importBooks(null, "Animal Farm", "Orwell", 10);
    }

    @Test
    void importBooks_WithoutParams_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/books/import"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(producer);
    }
}
