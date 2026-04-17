package org.darksamus86.library.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.darksamus86.library.book.common.handler.BookExceptionHandler;
import org.darksamus86.library.book.dto.request.CreateBookRequest;
import org.darksamus86.library.book.dto.response.ResponseGetBook;
import org.darksamus86.library.book.service.BookService;
import org.darksamus86.library.book.common.exceptions.BookNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // 1️⃣ Создаём контроллер с мок-сервисом
        BookController controller = new BookController(bookService);

        // 2️⃣ Создаём обработчик исключений (для обработки 404 и других ошибок)
        BookExceptionHandler exceptionHandler = new BookExceptionHandler();

        // 3️⃣ Настраиваем MockMvc БЕЗ явного указания конвертеров
        // ✅ Spring сам подключит JSON-поддержку через Jackson
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(exceptionHandler)  // Обработка исключений
                .build();

        // 4️⃣ ObjectMapper для сериализации тестовых данных
        objectMapper = new ObjectMapper();
    }

    @Test
    void getBookById_ShouldReturn200() throws Exception {
        // Given
        Long id = 1L;
        ResponseGetBook mockResponse = new ResponseGetBook(
                "Mock Title", "Desc",
                new BigDecimal("10"), null, null, 10, 2020
        );

        when(bookService.getBookById(eq(id))).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/books/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Mock Title"))
                .andExpect(jsonPath("$.price").value(10.0));
    }

    @Test
    void createBook_ShouldReturn201() throws Exception {
        // Given
        CreateBookRequest request = new CreateBookRequest(
                "New Book", "Desc", "1234567890123",
                new BigDecimal("10"), null, null, 5, 2023, null
        );

        ResponseGetBook createdResponse = new ResponseGetBook(
                "New Book", "Desc",
                new BigDecimal("10"), null, null, 5, 2023
        );

        when(bookService.createBook(any(CreateBookRequest.class)))
                .thenReturn(createdResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Book"))
                .andExpect(jsonPath("$.stockCount").value(5));
    }

    @Test
    void getBookById_ShouldReturn404_WhenNotFound() throws Exception {
        // Given
        Long id = 999L;
        when(bookService.getBookById(eq(id)))
                .thenThrow(new BookNotFoundException(id));

        // When & Then
        mockMvc.perform(get("/api/v1/books/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("BOOK_NOT_FOUND"));
    }
}