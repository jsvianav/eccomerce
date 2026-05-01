package co.edu.usbcali.ecommerceusb.mapper;

import co.edu.usbcali.ecommerceusb.dto.InventoryMovementResponse;
import co.edu.usbcali.ecommerceusb.model.InventoryMovement;

import java.util.List;

public class InventoryMovementMapper {

    public static InventoryMovementResponse modelToInventoryMovementResponse(InventoryMovement movement) {
        return InventoryMovementResponse.builder()
                .id(movement.getId())
                .productId(movement.getProduct() != null ? movement.getProduct().getId() : null)
                .orderId(movement.getOrder() != null ? movement.getOrder().getId() : null)
                .type(movement.getType() != null ? movement.getType().name() : null)
                .qty(movement.getQty())
                .build();
    }

    public static List<InventoryMovementResponse> modelToInventoryMovementResponseList(
            List<InventoryMovement> movements) {
        return movements.stream()
                .map(InventoryMovementMapper::modelToInventoryMovementResponse)
                .toList();
    }
}
