package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreateOrderItemRequest;
import co.edu.usbcali.ecommerceusb.dto.OrderItemResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateOrderItemRequest;
import co.edu.usbcali.ecommerceusb.service.OrderItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de items dentro de una orden de compra.
 * Expone los endpoints CRUD bajo la ruta base /orderItem.
 */
@RestController
@RequestMapping("/orderItem")
@Tag(name = "OrderItem", description = "Operaciones para la gestión de productos dentro de una orden")
public class OrderItemController {

    // Inyección del servicio que contiene la lógica de negocio de items de orden
    @Autowired
    private OrderItemService orderItemService;

    /**
     * Retorna la lista completa de items de orden registrados.
     * No requiere parámetros. Siempre retorna 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<OrderItemResponse>> getOrderItems() {
        // Delega al servicio y envuelve el resultado en un 200 OK
        return ResponseEntity.ok(orderItemService.getOrderItems());
    }

    /**
     * Busca un item de orden por su ID.
     * Retorna 200 OK si existe, o 400 Bad Request si el ID es inválido o no se encuentra.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderItemById(@PathVariable Integer id) {
        try {
            // Intenta buscar el item; retorna 200 si existe
            return ResponseEntity.ok(orderItemService.getOrderItemById(id));
        } catch (Exception e) {
            // Si el servicio lanza una excepción, retorna 400 con el mensaje de error
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Agrega un nuevo producto a una orden existente.
     * El lineTotal se calcula automáticamente (unitPriceSnapshot x quantity).
     * Retorna 200 OK con el item creado, o 400 si los datos son inválidos o el producto ya estaba en la orden.
     */
    @PostMapping
    public ResponseEntity<?> createOrderItem(@RequestBody CreateOrderItemRequest createOrderItemRequest) {
        try {
            // Pasa el request al servicio para que lo valide, calcule el total y persista
            return ResponseEntity.ok(orderItemService.createOrderItem(createOrderItemRequest));
        } catch (Exception e) {
            // Retorna 400 con el mensaje de validación si algo falla
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualiza cantidad y/o precio unitario de un item de orden existente.
     * El lineTotal se recalcula automáticamente al guardar.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderItem(@PathVariable Integer id, @RequestBody UpdateOrderItemRequest updateOrderItemRequest) {
        try {
            // Pasa el id y el request al servicio para que actualice y recalcule el total
            return ResponseEntity.ok(orderItemService.updateOrderItem(id, updateOrderItemRequest));
        } catch (Exception e) {
            // Retorna 400 con el mensaje de error si la operación falla
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
