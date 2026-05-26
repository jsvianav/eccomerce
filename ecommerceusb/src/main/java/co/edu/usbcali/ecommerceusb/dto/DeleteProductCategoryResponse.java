package co.edu.usbcali.ecommerceusb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO de respuesta para la eliminación de una asignación producto-categoría
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteProductCategoryResponse {

    private String message;
}
