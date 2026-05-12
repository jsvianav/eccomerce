package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreateInventoryMovementRequest;
import co.edu.usbcali.ecommerceusb.dto.InventoryMovementResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateInventoryMovementRequest;
import co.edu.usbcali.ecommerceusb.service.InventoryMovementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
     */
    @Operation(summary = "Listar todos los movimientos", description = "Retorna el historial completo de movimientos de inventario")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<InventoryMovementResponse>> getInventoryMovements() {
        return ResponseEntity.ok(inventoryMovementService.getInventoryMovements());
    }

    /**
     * Busca un movimiento de inventario por su ID.
     */
    @Operation(summary = "Buscar movimiento por ID", description = "Retorna un movimiento de inventario específico según su identificador")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Movimiento encontrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o movimiento no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getInventoryMovementById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(inventoryMovementService.getInventoryMovementById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Registra un nuevo movimiento de inventario.
     * El tipo puede ser DEBIT (salida), CREDIT (entrada), RESERVE (reserva) o RELEASE (liberación).
     */
    @Operation(summary = "Registrar movimiento de inventario", description = "Crea un nuevo movimiento: DEBIT (salida), CREDIT (entrada), RESERVE (reserva) o RELEASE (liberación)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Movimiento registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o tipo de movimiento no permitido")
    })
    @PostMapping
    public ResponseEntity<?> createInventoryMovement(@RequestBody CreateInventoryMovementRequest createInventoryMovementRequest) {
        try {
            return ResponseEntity.ok(inventoryMovementService.createInventoryMovement(createInventoryMovementRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualiza la cantidad o el tipo de un movimiento de inventario existente.
     */
    @Operation(summary = "Actualizar movimiento de inventario", description = "Modifica la cantidad o tipo de un movimiento de inventario existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Movimiento actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o datos incorrectos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInventoryMovement(@PathVariable Integer id, @RequestBody UpdateInventoryMovementRequest updateInventoryMovementRequest) {
        try {
            return ResponseEntity.ok(inventoryMovementService.updateInventoryMovement(id, updateInventoryMovementRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
