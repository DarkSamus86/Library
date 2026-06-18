package org.darksamus86.library.payment.entity;

import org.darksamus86.library.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentMethodEntityTest {

    @Test
    @DisplayName("Should build payment method with builder")
    void builder_ShouldCreatePaymentMethod() {
        User user = new User();
        user.setId(1L);

        PaymentMethod pm = PaymentMethod.builder()
                .id(10L)
                .user(user)
                .type("CARD")
                .provider("VISA")
                .accountNumber("****1234")
                .expiryDate("12/25")
                .isDefault(true)
                .build();

        assertThat(pm.getId()).isEqualTo(10L);
        assertThat(pm.getUser()).isEqualTo(user);
        assertThat(pm.getType()).isEqualTo("CARD");
        assertThat(pm.getProvider()).isEqualTo("VISA");
        assertThat(pm.getAccountNumber()).isEqualTo("****1234");
        assertThat(pm.getExpiryDate()).isEqualTo("12/25");
        assertThat(pm.getIsDefault()).isTrue();
    }

    @Test
    @DisplayName("Should set createdAt and default isDefault on PrePersist")
    void onCreate_ShouldSetDefaults() {
        PaymentMethod pm = new PaymentMethod();

        pm.onCreate();

        assertThat(pm.getCreatedAt()).isNotNull();
        assertThat(pm.getIsDefault()).isFalse();
    }

    @Test
    @DisplayName("Should not override isDefault if already set")
    void onCreate_ShouldNotOverrideIsDefault() {
        PaymentMethod pm = new PaymentMethod();
        pm.setIsDefault(true);

        pm.onCreate();

        assertThat(pm.getIsDefault()).isTrue();
    }
}
