package co.edu.usbcali.ecommerceusb.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "payments",
        schema = "public",
        indexes = {
                @Index(name = "uq_payments_idempotency_key", columnList = "idempotency_key", unique = true),
                @Index(name = "idx_payments_order",          columnList = "order_id")
                // uq_one_success_payment_per_order: índice parcial con WHERE,
                // no soportado por JPA → definir en migración (Flyway/Liquibase)
        }
)
@Check(constraints = "status IN ('SUCCEEDED', 'FAILED')")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "order_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_payments_order"),
            referencedColumnName = "id"
    )
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "text")
    private PaymentStatus status;

    @Column(name = "provider_ref", columnDefinition = "text")
    private String providerRef;

    @Column(name = "idempotency_key", nullable = false, columnDefinition = "text")
    private String idempotencyKey;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public enum PaymentStatus {
        SUCCEEDED, FAILED
    }
}
