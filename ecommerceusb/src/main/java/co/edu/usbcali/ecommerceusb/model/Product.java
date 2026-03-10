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
        name = "products",
        schema = "public",
        indexes = {
                @Index(name = "idx_products_available", columnList = "available"),
                @Index(name = "idx_products_price",     columnList = "price")
                // idx_products_fts: índice GIN con to_tsvector()
                // no soportado por JPA → definir en migración (Flyway/Liquibase)
        }
)
@Check(constraints = "price >= 0")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, columnDefinition = "text")
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(
            name = "price",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal price;

    @Column(name = "available", nullable = false, columnDefinition = "boolean default true")
    private Boolean available;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
