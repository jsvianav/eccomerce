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
        name = "inventory",
        schema = "public"
)
@Check(constraints = "stock >= 0")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_inventory_product"),
            referencedColumnName = "id"
    )
    private Product product;

    @Column(name = "stock", nullable = false, columnDefinition = "integer default 0")
    private Integer stock;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
