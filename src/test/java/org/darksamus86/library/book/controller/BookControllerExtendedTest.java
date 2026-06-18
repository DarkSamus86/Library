package org.darksamus86.library.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.darksamus86.library.book.common.exceptions.BookNotFoundException;
import org.darksamus86.library.book.common.handler.BookExceptionHandler;
import org.darksamus86.library.book.dto.request.UpdateBookRequest;
import org.darksamus86.library.book.dto.response.ResponseGetBook;
import org.darksamus86.library.book.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookControllerExtendedTest {

    @Mock
    private BookService bookService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        BookController controller = new BookController(bookService);
        BookExceptionHandler handler = new BookExceptionHandler();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(handler)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllBooks_WithPagination_ShouldReturn200() throws Exception {
        ResponseGetBook book = new ResponseGetBook(1L, "Book 1", null, BigDecimal.TEN, null, null, 5, 2023);
        Page<ResponseGetBook> page = new PageImpl<>(List.of(book), PageRequest.of(0, 10), 1);
        when(bookService.getAllBooks(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Book 1"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getAllBooksList_ShouldReturn200() throws Exception {
        ResponseGetBook book = new ResponseGetBook(1L, "Book 1", null, BigDecimal.TEN, null, null, 5, 2023);
        when(bookService.getAllBooks()).thenReturn(List.of(book));

        mockMvc.perform(get("/api/v1/books/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Book 1"));
    }

    @Test
    void updateBook_ShouldReturn200() throws Exception {
        UpdateBookRequest request = new UpdateBookRequest("Updated Title", null, null, null, null, null, null, null, null, null);
        ResponseGetBook response = new ResponseGetBook(1L, "Updated Title", null, BigDecimal.TEN, null, null, 5, 2023);
        when(bookService.updateBook(eq(1L), any(UpdateBookRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void partialUpdateBook_ShouldReturn200() throws Exception {
        UpdateBookRequest request = new UpdateBookRequest("Partial Title", null, null, null, null, null, null, null, null, null);
        ResponseGetBook response = new ResponseGetBook(1L, "Partial Title", null, BigDecimal.TEN, null, null, 5, 2023);
        when(bookService.updateBook(eq(1L), any(UpdateBookRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Partial Title"));
    }

    @Test
    void deleteBook_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService).deleteBook(1L);
    }

    @Test
    void hardDeleteBook_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/books/hard-delete/1"))
                .andExpect(status().isNoContent());

        verify(bookService).hardDeleteBook(1L);
    }

    @Test
    void searchBooks_ShouldReturn200() throws Exception {
        ResponseGetBook book = new ResponseGetBook(1L, "Java Book", null, BigDecimal.TEN, null, null, 5, 2023);
        when(bookService.searchBooksByTitle("java")).thenReturn(List.of(book));

        mockMvc.perform(get("/api/v1/books/search").param("title", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java Book"));
    }

    @Test
    void createBook_ShouldReturn400_WhenValidationFails() throws Exception {
        String invalidRequest = "{\"title\":\"\",\"price\":-1}";

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBookPrices_ShouldReturn400_WhenPriceIsNull() throws Exception {
        String invalidRequest = "{\"price\":null}";

        mockMvc.perform(patch("/api/v1/books/1/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }
}
