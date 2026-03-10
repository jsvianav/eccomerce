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
        name = "order_items",
        schema = "public",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_order_product", columnNames = {"order_id", "product_id"})
        },
        indexes = {
                @Index(name = "idx_order_items_order", columnList = "order_id")
        }
)
@Check(constraints = "quantity > 0 AND unit_price_snapshot >= 0 AND line_total >= 0")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "order_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_items_order"),
            referencedColumnName = "id"
    )
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_items_product"),
            referencedColumnName = "id"
    )
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(
            name = "unit_price_snapshot",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal unitPriceSnapshot;

    @Column(
            name = "line_total",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal lineTotal;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
