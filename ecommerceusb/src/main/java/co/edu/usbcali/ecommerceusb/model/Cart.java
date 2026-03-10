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
        name = "carts",
        schema = "public",
        indexes = {
                @Index(name = "idx_carts_user_status", columnList = "user_id, status")
                // uq_active_cart_per_user: índice único parcial WHERE status = 'ACTIVE'
                // no soportado por JPA → definir en migración (Flyway/Liquibase)
        }
)
@Check(constraints = "status IN ('ACTIVE', 'CHECKED_OUT', 'ABANDONED')")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_carts_user"),
            referencedColumnName = "id"
    )
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "text")
    private CartStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public enum CartStatus {
        ACTIVE, CHECKED_OUT, ABANDONED
    }
}
