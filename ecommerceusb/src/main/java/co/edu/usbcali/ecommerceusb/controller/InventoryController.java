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

    // Inyección del servicio que contiene la lógica de negocio de inventarios
    @Autowired
    private InventoryService inventoryService;

    /**
     * Retorna la lista completa de registros de inventario.
     * No requiere parámetros. Siempre retorna 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getInventories() {
        // Delega al servicio y envuelve el resultado en un 200 OK
        return ResponseEntity.ok(inventoryService.getInventories());
    }

    /**
     * Busca un registro de inventario por su ID.
     * Retorna 200 OK si existe, o 400 Bad Request si el ID es inválido o no se encuentra.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getInventoryById(@PathVariable Integer id) {
        try {
            // Intenta buscar el inventario; retorna 200 si existe
            return ResponseEntity.ok(inventoryService.getInventoryById(id));
        } catch (Exception e) {
            // Si el servicio lanza una excepción, retorna 400 con el mensaje de error
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Crea un nuevo registro de inventario para un producto.
     * Solo se permite un inventario por producto (relación única 1 a 1).
     * Retorna 200 OK con el inventario creado, o 400 si el producto ya tiene uno.
     */
    @PostMapping
    public ResponseEntity<?> createInventory(@RequestBody CreateInventoryRequest createInventoryRequest) {
        try {
            // Pasa el request al servicio para que valide la unicidad y persista
            return ResponseEntity.ok(inventoryService.createInventory(createInventoryRequest));
        } catch (Exception e) {
            // Retorna 400 con el mensaje de validación si algo falla
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualiza el stock de un inventario existente.
     * Solo modifica el campo enviado en el body.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInventory(@PathVariable Integer id, @RequestBody UpdateInventoryRequest updateInventoryRequest) {
        try {
            // Pasa el id y el request al servicio para que actualice el stock
            return ResponseEntity.ok(inventoryService.updateInventory(id, updateInventoryRequest));
        } catch (Exception e) {
            // Retorna 400 con el mensaje de error si la operación falla
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteInventoryResponse> deleteInventory(
            @PathVariable Integer id) throws Exception {
        return new ResponseEntity<>(inventoryService.deleteInventory(id), HttpStatus.OK);
    }
}
