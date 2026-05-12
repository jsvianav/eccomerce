package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreateInventoryMovementRequest;
import co.edu.usbcali.ecommerceusb.dto.InventoryMovementResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateInventoryMovementRequest;
import co.edu.usbcali.ecommerceusb.service.InventoryMovementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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
@Tag(name = "InventoryMovement", description = "Operaciones para registrar movimientos de inventario (DEBIT, CREDIT, RESERVE, RELEASE)")
public class InventoryMovementController {

    // Inyección del servicio que contiene la lógica de negocio de movimientos de inventario
    @Autowired
    private InventoryMovementService inventoryMovementService;

    /**
     * Retorna la lista completa de movimientos de inventario registrados.
     * No requiere parámetros. Siempre retorna 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<InventoryMovementResponse>> getInventoryMovements() {
        // Delega al servicio y envuelve el resultado en un 200 OK
        return ResponseEntity.ok(inventoryMovementService.getInventoryMovements());
    }

    /**
     * Busca un movimiento de inventario por su ID.
     * Retorna 200 OK si existe, o 400 Bad Request si el ID es inválido o no se encuentra.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getInventoryMovementById(@PathVariable Integer id) {
        try {
            // Intenta buscar el movimiento; retorna 200 si existe
            return ResponseEntity.ok(inventoryMovementService.getInventoryMovementById(id));
        } catch (Exception e) {
            // Si el servicio lanza una excepción, retorna 400 con el mensaje de error
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Registra un nuevo movimiento de inventario.
     * El tipo puede ser DEBIT (salida), CREDIT (entrada), RESERVE (reserva) o RELEASE (liberación).
     * El orderId es opcional; solo se asocia a una orden si viene en el request.
     */
    @PostMapping
    public ResponseEntity<?> createInventoryMovement(@RequestBody CreateInventoryMovementRequest createInventoryMovementRequest) {
        try {
            // Pasa el request al servicio para que valide el tipo y persista el movimiento
            return ResponseEntity.ok(inventoryMovementService.createInventoryMovement(createInventoryMovementRequest));
        } catch (Exception e) {
            // Retorna 400 con el mensaje de validación si algo falla
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualiza la cantidad o el tipo de un movimiento de inventario existente.
     * Solo modifica los campos enviados en el body.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInventoryMovement(@PathVariable Integer id, @RequestBody UpdateInventoryMovementRequest updateInventoryMovementRequest) {
        try {
            // Pasa el id y el request al servicio para que actualice solo los campos recibidos
            return ResponseEntity.ok(inventoryMovementService.updateInventoryMovement(id, updateInventoryMovementRequest));
        } catch (Exception e) {
            // Retorna 400 con el mensaje de error si la operación falla
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
