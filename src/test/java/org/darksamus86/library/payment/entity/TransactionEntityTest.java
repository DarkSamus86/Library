package org.darksamus86.library.payment.entity;

import org.darksamus86.library.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionEntityTest {

    @Test
    @DisplayName("Should build transaction with builder")
    void builder_ShouldCreateTransaction() {
        User user = new User();
        user.setId(1L);
        PaymentMethod pm = new PaymentMethod();
        pm.setId(2L);

        Transaction transaction = Transaction.builder()
                .id(10L)
                .user(user)
                .paymentMethod(pm)
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .status("SUCCESS")
                .build();

        assertThat(transaction.getId()).isEqualTo(10L);
        assertThat(transaction.getUser()).isEqualTo(user);
        assertThat(transaction.getPaymentMethod()).isEqualTo(pm);
        assertThat(transaction.getAmount()).isEqualTo(new BigDecimal("100.00"));
        assertThat(transaction.getCurrency()).isEqualTo("USD");
        assertThat(transaction.getStatus()).isEqualTo("SUCCESS");
    }

    @Test
    @DisplayName("Should set createdAt on PrePersist")
    void onCreate_ShouldSetTimestamp() {
        Transaction transaction = new Transaction();

        transaction.onCreate();

        assertThat(transaction.getCreatedAt()).isNotNull();
    }
}
