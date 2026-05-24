package co.edu.usbcali.ecommerceusb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO de respuesta para la eliminación de un OrderItem
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteOrderItemResponse {
    // Mensaje de confirmación que indica el resultado de la eliminación
    private String message;
}
