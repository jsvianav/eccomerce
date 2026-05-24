package co.edu.usbcali.ecommerceusb.mapper;

import co.edu.usbcali.ecommerceusb.dto.CreateDocumentTypeRequest;
import co.edu.usbcali.ecommerceusb.dto.DocumentTypeResponse;
import co.edu.usbcali.ecommerceusb.model.DocumentType;

import java.time.OffsetDateTime;
import java.util.List;

public class DocumentTypeMapper {

    /**
     * Convierte un modelo DocumentType en su DTO de respuesta DocumentTypeResponse.
     */
    public static DocumentTypeResponse modelToDocumentTypeResponse(DocumentType documentType) {
        return DocumentTypeResponse.builder()
                .id(documentType.getId())
                .code(documentType.getCode())
                .name(documentType.getName())
                .build();
    }

    /**
     * Convierte una lista de modelos DocumentType en una lista de DTOs DocumentTypeResponse.
     * Usa streams para iterar la lista y mapear cada elemento.
     */
    public static List<DocumentTypeResponse> modelToDocumentTypeResponseList(
            List<DocumentType> documentTypes) {
        return documentTypes.stream().map(DocumentTypeMapper::modelToDocumentTypeResponse).toList();
    }

    /**
     * Convierte un CreateDocumentTypeRequest en el modelo DocumentType.
     * Establece la fecha de creación automáticamente con el momento actual.
     */
    public static DocumentType createDocumentTypeRequestToDocumentType(CreateDocumentTypeRequest req) {
        return DocumentType.builder()
                // Asigna el código del tipo de documento (ej: "CC", "NIT")
                .code(req.getCode())
                // Asigna el nombre descriptivo del tipo de documento
                .name(req.getName())
                // Establece la fecha y hora actual como fecha de creación
                .createdAt(OffsetDateTime.now())
                .build();
    }
}
