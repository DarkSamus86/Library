package org.darksamus86.library.book.mapper;

import org.darksamus86.library.book.dto.request.CreateBookRequest;
import org.darksamus86.library.book.dto.request.UpdateBookRequest;
import org.darksamus86.library.book.dto.response.ResponseGetBook;
import org.darksamus86.library.book.entity.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BookMapperTest {

    private final BookMapper bookMapper = new BookMapper();

    @Test
    @DisplayName("Should map Book entity to ResponseGetBook")
    void toResponse_ShouldMapCorrectly() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setDescription("Test Description");
        book.setPricePurchase(new BigDecimal("10.00"));
        book.setPriceRental(new BigDecimal("2.00"));
        book.setDepositAmount(new BigDecimal("5.00"));
        book.setHasPhysical(true);
        book.setHasDigital(true);
        book.setPhysicalInventory(5);
        book.setDigitalLicenses(-1);
        book.setIsAvailableForRent(true);
        book.setIsAvailableForPurchase(true);
        book.setPublishedYear(2023);
        book.setCoverImageUrl("http://cover.url");
        book.setTotalRentalsCount(10);
        book.setTotalPurchasesCount(5);

        ResponseGetBook result = bookMapper.toResponse(book);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Test Book");
        assertThat(result.description()).isEqualTo("Test Description");
        assertThat(result.pricePurchase()).isEqualTo(new BigDecimal("10.00"));
        assertThat(result.priceRental()).isEqualTo(new BigDecimal("2.00"));
        assertThat(result.depositAmount()).isEqualTo(new BigDecimal("5.00"));
        assertThat(result.hasPhysical()).isTrue();
        assertThat(result.hasDigital()).isTrue();
        assertThat(result.physicalInventory()).isEqualTo(5);
        assertThat(result.digitalLicenses()).isEqualTo(-1);
        assertThat(result.isAvailableForRent()).isTrue();
        assertThat(result.isAvailableForPurchase()).isTrue();
        assertThat(result.publishedYear()).isEqualTo(2023);
        assertThat(result.coverImageUrl()).isEqualTo("http://cover.url");
        assertThat(result.totalRentalsCount()).isEqualTo(10);
        assertThat(result.totalPurchasesCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should return null when book is null")
    void toResponse_WhenNullBook_ShouldReturnNull() {
        assertThat(bookMapper.toResponse(null)).isNull();
    }

    @Test
    @DisplayName("Should map CreateBookRequest to Book entity")
    void toEntity_ShouldMapCorrectly() {
        CreateBookRequest request = new CreateBookRequest(
                "New Book", "Description", "1234567890",
                new BigDecimal("10.00"), new BigDecimal("2.00"), new BigDecimal("5.00"),
                5, -1, true, true, 2023, "http://cover.url"
        );

        Book result = bookMapper.toEntity(request);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("New Book");
        assertThat(result.getDescription()).isEqualTo("Description");
        assertThat(result.getIsbn()).isEqualTo("1234567890");
        assertThat(result.getPricePurchase()).isEqualTo(new BigDecimal("10.00"));
        assertThat(result.getPriceRental()).isEqualTo(new BigDecimal("2.00"));
        assertThat(result.getDepositAmount()).isEqualTo(new BigDecimal("5.00"));
        assertThat(result.getPhysicalInventory()).isEqualTo(5);
        assertThat(result.getDigitalLicenses()).isEqualTo(-1);
        assertThat(result.getHasPhysical()).isTrue();
        assertThat(result.getHasDigital()).isTrue();
        assertThat(result.getPublishedYear()).isEqualTo(2023);
        assertThat(result.getCoverImageUrl()).isEqualTo("http://cover.url");
        assertThat(result.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should return null when request is null")
    void toEntity_WhenNullRequest_ShouldReturnNull() {
        assertThat(bookMapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("Should update entity from UpdateBookRequest")
    void updateEntityFromRequest_ShouldUpdateFields() {
        Book book = new Book();
        book.setTitle("Old Title");
        book.setDescription("Old Description");
        book.setIsbn("old-isbn");
        book.setPricePurchase(BigDecimal.ZERO);
        book.setPhysicalInventory(0);

        UpdateBookRequest request = new UpdateBookRequest(
                "New Title", "New Description", "new-isbn",
                new BigDecimal("20.00"), new BigDecimal("3.00"), new BigDecimal("10.00"),
                10, -1, true, true, true, true, 2024, "http://new-cover.url", true
        );

        bookMapper.updateEntityFromRequest(request, book);

        assertThat(book.getTitle()).isEqualTo("New Title");
        assertThat(book.getDescription()).isEqualTo("New Description");
        assertThat(book.getIsbn()).isEqualTo("new-isbn");
        assertThat(book.getPricePurchase()).isEqualTo(new BigDecimal("20.00"));
        assertThat(book.getPriceRental()).isEqualTo(new BigDecimal("3.00"));
        assertThat(book.getDepositAmount()).isEqualTo(new BigDecimal("10.00"));
        assertThat(book.getPhysicalInventory()).isEqualTo(10);
        assertThat(book.getDigitalLicenses()).isEqualTo(-1);
        assertThat(book.getHasPhysical()).isTrue();
        assertThat(book.getHasDigital()).isTrue();
        assertThat(book.getIsAvailableForRent()).isTrue();
        assertThat(book.getIsAvailableForPurchase()).isTrue();
        assertThat(book.getPublishedYear()).isEqualTo(2024);
        assertThat(book.getCoverImageUrl()).isEqualTo("http://new-cover.url");
        assertThat(book.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should not update null fields in partial update")
    void updateEntityFromRequest_WhenNullFields_ShouldNotUpdate() {
        Book book = new Book();
        book.setTitle("Original Title");
        book.setPricePurchase(new BigDecimal("10.00"));

        UpdateBookRequest request = new UpdateBookRequest(
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null
        );

        bookMapper.updateEntityFromRequest(request, book);

        assertThat(book.getTitle()).isEqualTo("Original Title");
        assertThat(book.getPricePurchase()).isEqualTo(new BigDecimal("10.00"));
    }

    @Test
    @DisplayName("Should do nothing when request is null")
    void updateEntityFromRequest_WhenNullRequest_ShouldDoNothing() {
        Book book = new Book();
        book.setTitle("Title");

        bookMapper.updateEntityFromRequest(null, book);

        assertThat(book.getTitle()).isEqualTo("Title");
    }

    @Test
    @DisplayName("Should do nothing when book is null")
    void updateEntityFromRequest_WhenNullBook_ShouldDoNothing() {
        UpdateBookRequest request = new UpdateBookRequest(
                "Title", null, null, null, null, null, null, null, null, null, null, null, null, null, null
        );

        bookMapper.updateEntityFromRequest(request, null);
    }
}
