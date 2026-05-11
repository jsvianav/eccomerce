package co.edu.usbcali.ecommerceusb.service;

import co.edu.usbcali.ecommerceusb.dto.CreateInventoryMovementRequest;
import co.edu.usbcali.ecommerceusb.dto.InventoryMovementResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateInventoryMovementRequest;

import java.util.List;

public interface InventoryMovementService {
    List<InventoryMovementResponse> getInventoryMovements();
    InventoryMovementResponse getInventoryMovementById(Integer id) throws Exception;
    InventoryMovementResponse createInventoryMovement(CreateInventoryMovementRequest createInventoryMovementRequest) throws Exception;
    InventoryMovementResponse updateInventoryMovement(Integer id, UpdateInventoryMovementRequest updateInventoryMovementRequest) throws Exception;
}
