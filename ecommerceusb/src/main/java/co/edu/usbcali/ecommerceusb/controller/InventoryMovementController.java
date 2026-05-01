package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreateInventoryMovementRequest;
import co.edu.usbcali.ecommerceusb.dto.InventoryMovementResponse;
import co.edu.usbcali.ecommerceusb.service.InventoryMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory-movements")
public class InventoryMovementController {

    @Autowired
    private InventoryMovementService inventoryMovementService;

    @GetMapping
    public ResponseEntity<List<InventoryMovementResponse>> getInventoryMovements() {
        return ResponseEntity.ok(inventoryMovementService.getInventoryMovements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryMovementResponse> getInventoryMovementById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(inventoryMovementService.getInventoryMovementById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<InventoryMovementResponse> createInventoryMovement(
            @RequestBody CreateInventoryMovementRequest createInventoryMovementRequest) {
        try {
            return ResponseEntity.ok(
                    inventoryMovementService.createInventoryMovement(createInventoryMovementRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
