package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreateInventoryRequest;
import co.edu.usbcali.ecommerceusb.dto.InventoryResponse;
import co.edu.usbcali.ecommerceusb.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventories")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getInventories() {
        return ResponseEntity.ok(inventoryService.getInventories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getInventoryById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(inventoryService.getInventoryById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(
            @RequestBody CreateInventoryRequest createInventoryRequest) {
        try {
            return ResponseEntity.ok(inventoryService.createInventory(createInventoryRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
