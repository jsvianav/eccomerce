package co.edu.usbcali.ecommerceusb.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "inventory_movements",
        schema = "public",
        indexes = {
                @Index(name = "idx_inventory_movements_product_created_at", columnList = "product_id ASC, created_at DESC"),
                @Index(name = "idx_inventory_movements_order",              columnList = "order_id")
        }
)
@Check(constraints = "type IN ('DEBIT', 'CREDIT', 'RESERVE', 'RELEASE') AND qty > 0")
public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_inv_mov_product"),
            referencedColumnName = "id"
    )
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "order_id",
            foreignKey = @ForeignKey(name = "fk_inv_mov_order"),
            referencedColumnName = "id"
    )
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "text")
    private MovementType type;

    @Column(name = "qty", nullable = false)
    private Integer qty;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public enum MovementType {
        DEBIT, CREDIT, RESERVE, RELEASE
    }
}
