package co.edu.usbcali.ecommerceusb.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "orders",
        schema = "public",
        indexes = {
                @Index(name = "idx_orders_user_created_at",   columnList = "user_id ASC, created_at DESC"),
                @Index(name = "idx_orders_status_created_at", columnList = "status ASC, created_at DESC")
        }
)
@Check(constraints = "status IN ('CREATED', 'PAID', 'CANCELLED') AND total_amount >= 0")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_orders_user"),
            referencedColumnName = "id"
    )
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "text")
    private OrderStatus status;

    @Column(
            name = "total_amount",
            nullable = false,
            precision = 12,
            scale = 2,
            columnDefinition = "numeric(12,2) default 0"
    )
    private BigDecimal totalAmount;

    @Column(name = "currency", nullable = false, columnDefinition = "text default 'COP'")
    private String currency;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "paid_at")
    private OffsetDateTime paidAt;

    @Column(name = "cancelled_at")
    private OffsetDateTime cancelledAt;

    public enum OrderStatus {
        CREATED, PAID, CANCELLED
    }
}
