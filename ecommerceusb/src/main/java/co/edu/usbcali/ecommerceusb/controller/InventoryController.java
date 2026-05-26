package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreateInventoryRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteInventoryResponse;
import co.edu.usbcali.ecommerceusb.dto.InventoryResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateInventoryRequest;
import co.edu.usbcali.ecommerceusb.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de inventarios de productos.
 * Expone los endpoints CRUD bajo la ruta base /inventory.
 * Cada producto tiene un único registro de inventario (relación 1 a 1).
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    /** Retorna la lista completa de registros de inventario. */
    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getInventories() {
        return ResponseEntity.ok(inventoryService.getInventories());
    }

    /** Busca un registro de inventario por su ID. */
    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getInventoryById(@PathVariable Integer id) {
        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }

    /**
     * Crea un nuevo registro de inventario para un producto.
     * Solo se permite un inventario por producto (relación única 1 a 1). Retorna 201 Created.
     */
    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(@RequestBody CreateInventoryRequest createInventoryRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.createInventory(createInventoryRequest));
    }

    /** Actualiza el stock de un inventario existente. */
    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponse> updateInventory(@PathVariable Integer id, @RequestBody UpdateInventoryRequest updateInventoryRequest) {
        return ResponseEntity.ok(inventoryService.updateInventory(id, updateInventoryRequest));
    }

    /** Elimina un inventario por su ID. */
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteInventoryResponse> deleteInventory(@PathVariable Integer id) {
        return ResponseEntity.ok(inventoryService.deleteInventory(id));
    }
}
