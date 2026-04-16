package org.darksamus86.library.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.darksamus86.library.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_methods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "type")
    private String type; // CARD, PAYPAL

    @Column(name = "provider")
    private String provider; // VISA, MASTER

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "expiry_date")
    private String expiryDate;

    @Column(name = "is_default")
    private Boolean isDefault;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isDefault == null) this.isDefault = false;
    }
}