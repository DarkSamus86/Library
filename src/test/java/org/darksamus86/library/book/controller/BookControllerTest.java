package org.darksamus86.library.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.darksamus86.library.book.common.handler.BookExceptionHandler;
import org.darksamus86.library.book.dto.request.BookPricesRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
        BookController controller = new BookController(bookService);
        BookExceptionHandler exceptionHandler = new BookExceptionHandler();
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(exceptionHandler)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getBookById_ShouldReturn200() throws Exception {
        ResponseGetBook mockResponse = new ResponseGetBook(
                1L, "Mock Title", "Desc",
                new BigDecimal("10"), null, null, true, true, 10, -1, true, true, 2020, null, 0, 0
        );

        when(bookService.getBookById(eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/books/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Mock Title"))
                .andExpect(jsonPath("$.pricePurchase").value(10.0));
    }

    @Test
    void createBook_ShouldReturn201() throws Exception {
        CreateBookRequest request = new CreateBookRequest(
                "New Book", "Desc", "1234567890123",
                new BigDecimal("10"), null, null, 5, -1, true, true, 2023, null
        );

        ResponseGetBook createdResponse = new ResponseGetBook(
                1L, "New Book", "Desc",
                new BigDecimal("10"), null, null, true, true, 5, -1, true, true, 2023, null, 0, 0
        );

        when(bookService.createBook(any(CreateBookRequest.class)))
                .thenReturn(createdResponse);

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Book"))
                .andExpect(jsonPath("$.physicalInventory").value(5));
    }

    @Test
    void getBookById_ShouldReturn404_WhenNotFound() throws Exception {
        Long id = 999L;
        when(bookService.getBookById(eq(id)))
                .thenThrow(new BookNotFoundException(id));

        mockMvc.perform(get("/api/v1/books/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updateBookPrices_ShouldReturn200() throws Exception {
        Long id = 1L;
        BookPricesRequest request = new BookPricesRequest(
                new BigDecimal("19.99"), new BigDecimal("3.99"), new BigDecimal("10.00")
        );
        ResponseGetBook response = new ResponseGetBook(
                1L, "Book Title", "Desc",
                new BigDecimal("19.99"), new BigDecimal("3.99"), new BigDecimal("10.00"), true, true, 5, -1, true, true, 2023, null, 0, 0
        );

        when(bookService.updateBookPrices(eq(id), any(BookPricesRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/books/{id}/prices", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pricePurchase").value(19.99))
                .andExpect(jsonPath("$.priceRental").value(3.99))
                .andExpect(jsonPath("$.depositAmount").value(10.00));
    }
}
