package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreateInventoryMovementRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteInventoryMovementResponse;
import co.edu.usbcali.ecommerceusb.dto.InventoryMovementResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateInventoryMovementRequest;
import co.edu.usbcali.ecommerceusb.service.InventoryMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de movimientos de inventario.
 * Registra entradas (CREDIT), salidas (DEBIT), reservas (RESERVE) y liberaciones (RELEASE).
 * Expone los endpoints CRUD bajo la ruta base /inventoryMovement.
 */
@RestController
@RequestMapping("/inventoryMovement")
public class InventoryMovementController {

    @Autowired
    private InventoryMovementService inventoryMovementService;

    /** Retorna la lista completa de movimientos de inventario registrados. */
    @GetMapping
    public ResponseEntity<List<InventoryMovementResponse>> getInventoryMovements() {
        return ResponseEntity.ok(inventoryMovementService.getInventoryMovements());
    }

    /** Busca un movimiento de inventario por su ID. */
    @GetMapping("/{id}")
    public ResponseEntity<InventoryMovementResponse> getInventoryMovementById(@PathVariable Integer id) {
        return ResponseEntity.ok(inventoryMovementService.getInventoryMovementById(id));
    }

    /**
     * Registra un nuevo movimiento de inventario.
     * El tipo puede ser DEBIT, CREDIT, RESERVE o RELEASE. Retorna 201 Created.
     * El orderId es opcional; solo se asocia a una orden si viene en el request.
     */
    @PostMapping
    public ResponseEntity<InventoryMovementResponse> createInventoryMovement(@RequestBody CreateInventoryMovementRequest createInventoryMovementRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryMovementService.createInventoryMovement(createInventoryMovementRequest));
    }

    /** Actualiza la cantidad o el tipo de un movimiento de inventario existente. */
    @PutMapping("/{id}")
    public ResponseEntity<InventoryMovementResponse> updateInventoryMovement(@PathVariable Integer id, @RequestBody UpdateInventoryMovementRequest updateInventoryMovementRequest) {
        return ResponseEntity.ok(inventoryMovementService.updateInventoryMovement(id, updateInventoryMovementRequest));
    }

    /** Elimina un movimiento de inventario por su ID. */
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteInventoryMovementResponse> deleteInventoryMovement(@PathVariable Integer id) {
        return ResponseEntity.ok(inventoryMovementService.deleteInventoryMovement(id));
    }
}
