package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreateInventoryRequest;
import co.edu.usbcali.ecommerceusb.dto.InventoryResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateInventoryRequest;
import co.edu.usbcali.ecommerceusb.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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
@Tag(name = "Inventory", description = "Operaciones para la gestión del inventario de productos")
public class InventoryController {

    // Inyección del servicio que contiene la lógica de negocio de inventarios
    @Autowired
    private InventoryService inventoryService;

    /**
     * Retorna la lista completa de registros de inventario.
     */
    @Operation(summary = "Listar todos los inventarios", description = "Retorna el stock actual de todos los productos")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getInventories() {
        return ResponseEntity.ok(inventoryService.getInventories());
    }

    /**
     * Busca un registro de inventario por su ID.
     */
    @Operation(summary = "Buscar inventario por ID", description = "Retorna el stock de un producto según el ID del registro de inventario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inventario encontrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o inventario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getInventoryById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(inventoryService.getInventoryById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Crea un nuevo registro de inventario para un producto.
     * Solo se permite un inventario por producto (relación única).
     */
    @Operation(summary = "Crear inventario", description = "Registra el stock inicial de un producto; cada producto solo puede tener un inventario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inventario creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o el producto ya tiene inventario")
    })
    @PostMapping
    public ResponseEntity<?> createInventory(@RequestBody CreateInventoryRequest createInventoryRequest) {
        try {
            return ResponseEntity.ok(inventoryService.createInventory(createInventoryRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualiza el stock de un inventario existente.
     */
    @Operation(summary = "Actualizar inventario", description = "Modifica la cantidad en stock de un inventario existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inventario actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o datos incorrectos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInventory(@PathVariable Integer id, @RequestBody UpdateInventoryRequest updateInventoryRequest) {
        try {
            return ResponseEntity.ok(inventoryService.updateInventory(id, updateInventoryRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
