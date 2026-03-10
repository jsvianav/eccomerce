package co.edu.usbcali.ecommerceusb.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "users",
        schema = "public",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_users_document", columnNames = {"document_type_id", "document_number"})
        },
        indexes = {
                @Index(name = "idx_users_document", columnList = "document_type_id, document_number"),
                @Index(name = "idx_users_country",  columnList = "country")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "full_name", nullable = false, columnDefinition = "text")
    private String fullName;

    @Column(name = "phone", columnDefinition = "text")
    private String phone;

    @Column(name = "email", nullable = false, unique = true, columnDefinition = "text")
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "document_type_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_users_document_type"),
            referencedColumnName = "id"
    )
    private DocumentType documentType;

    @Column(name = "document_number", nullable = false, columnDefinition = "text")
    private String documentNumber;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "country", nullable = false, columnDefinition = "text")
    private String country;

    @Column(name = "address", columnDefinition = "text")
    private String address;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
