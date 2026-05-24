package co.edu.usbcali.ecommerceusb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO que contiene los datos necesarios para crear un nuevo tipo de documento
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateDocumentTypeRequest {

    // Código único del tipo de documento (ej: "CC", "NIT")
    private String code;

    // Nombre descriptivo del tipo de documento (ej: "Cédula de ciudadanía")
    private String name;
}
