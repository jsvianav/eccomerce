package co.edu.usbcali.ecommerceusb.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "document_types", schema = "public")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "code", nullable = false, unique = true, columnDefinition = "text")  // + columnDefinition
    private String code;

    @Column(name = "name", nullable = false, columnDefinition = "text")                 // + columnDefinition
    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)                   // + updatable = false
    private OffsetDateTime createdAt;
}
