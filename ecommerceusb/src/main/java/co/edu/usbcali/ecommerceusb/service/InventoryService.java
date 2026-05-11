package co.edu.usbcali.ecommerceusb.service;

import co.edu.usbcali.ecommerceusb.dto.CreateInventoryRequest;
import co.edu.usbcali.ecommerceusb.dto.InventoryResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateInventoryRequest;

import java.util.List;

public interface InventoryService {
    List<InventoryResponse> getInventories();
    InventoryResponse getInventoryById(Integer id) throws Exception;
    InventoryResponse createInventory(CreateInventoryRequest createInventoryRequest) throws Exception;
    InventoryResponse updateInventory(Integer id, UpdateInventoryRequest updateInventoryRequest) throws Exception;
}
