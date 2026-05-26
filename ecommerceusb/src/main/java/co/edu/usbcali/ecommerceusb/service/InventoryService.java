package co.edu.usbcali.ecommerceusb.service;

import co.edu.usbcali.ecommerceusb.dto.CreateInventoryRequest;
import co.edu.usbcali.ecommerceusb.dto.InventoryResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateInventoryRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteInventoryResponse;
import java.util.List;

public interface InventoryService {
    List<InventoryResponse> getInventories();
    InventoryResponse getInventoryById(Integer id);
    InventoryResponse createInventory(CreateInventoryRequest createInventoryRequest);
    InventoryResponse updateInventory(Integer id, UpdateInventoryRequest updateInventoryRequest);
    DeleteInventoryResponse deleteInventory(Integer id);
}
