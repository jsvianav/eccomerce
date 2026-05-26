package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreateOrderItemRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteOrderItemResponse;
import co.edu.usbcali.ecommerceusb.dto.OrderItemResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateOrderItemRequest;
import co.edu.usbcali.ecommerceusb.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de items dentro de una orden de compra.
 * Expone los endpoints CRUD bajo la ruta base /orderItem.
 */
@RestController
@RequestMapping("/orderItem")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    /** Retorna la lista completa de items de orden registrados. */
    @GetMapping
    public ResponseEntity<List<OrderItemResponse>> getOrderItems() {
        return ResponseEntity.ok(orderItemService.getOrderItems());
    }

    /** Busca un item de orden por su ID. */
    @GetMapping("/{id}")
    public ResponseEntity<OrderItemResponse> getOrderItemById(@PathVariable Integer id) {
        return ResponseEntity.ok(orderItemService.getOrderItemById(id));
    }

    /**
     * Agrega un nuevo producto a una orden existente.
     * El lineTotal se calcula automáticamente (unitPriceSnapshot x quantity). Retorna 201 Created.
     */
    @PostMapping
    public ResponseEntity<OrderItemResponse> createOrderItem(@RequestBody CreateOrderItemRequest createOrderItemRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderItemService.createOrderItem(createOrderItemRequest));
    }

    /** Actualiza cantidad y/o precio unitario de un item de orden existente. */
    @PutMapping("/{id}")
    public ResponseEntity<OrderItemResponse> updateOrderItem(@PathVariable Integer id, @RequestBody UpdateOrderItemRequest updateOrderItemRequest) {
        return ResponseEntity.ok(orderItemService.updateOrderItem(id, updateOrderItemRequest));
    }

    /** Elimina un item de orden por su ID. */
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteOrderItemResponse> deleteOrderItem(@PathVariable Integer id) {
        return ResponseEntity.ok(orderItemService.deleteOrderItem(id));
    }
}
