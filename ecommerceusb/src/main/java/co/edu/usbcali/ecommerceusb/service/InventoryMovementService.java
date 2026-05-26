package co.edu.usbcali.ecommerceusb.service;

import co.edu.usbcali.ecommerceusb.dto.CreateInventoryMovementRequest;
import co.edu.usbcali.ecommerceusb.dto.InventoryMovementResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateInventoryMovementRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteInventoryMovementResponse;
import java.util.List;

public interface InventoryMovementService {
    List<InventoryMovementResponse> getInventoryMovements();
    InventoryMovementResponse getInventoryMovementById(Integer id);
    InventoryMovementResponse createInventoryMovement(CreateInventoryMovementRequest createInventoryMovementRequest);
    InventoryMovementResponse updateInventoryMovement(Integer id, UpdateInventoryMovementRequest updateInventoryMovementRequest);
    DeleteInventoryMovementResponse deleteInventoryMovement(Integer id);
}
