package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreateOrderItemRequest;
import co.edu.usbcali.ecommerceusb.dto.OrderItemResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateOrderItemRequest;
import co.edu.usbcali.ecommerceusb.service.OrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
     */
    @Operation(summary = "Listar todos los items de orden", description = "Retorna todos los productos incluidos en órdenes de compra")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<OrderItemResponse>> getOrderItems() {
        return ResponseEntity.ok(orderItemService.getOrderItems());
    }

    /**
     * Busca un item de orden por su ID.
     */
    @Operation(summary = "Buscar item de orden por ID", description = "Retorna un item específico de una orden según su identificador")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item encontrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o item no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderItemById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(orderItemService.getOrderItemById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Agrega un nuevo producto a una orden existente.
     */
    @Operation(summary = "Agregar item a una orden", description = "Agrega un producto a una orden de compra, calculando automáticamente el lineTotal")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item agregado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o producto ya existe en la orden")
    })
    @PostMapping
    public ResponseEntity<?> createOrderItem(@RequestBody CreateOrderItemRequest createOrderItemRequest) {
        try {
            return ResponseEntity.ok(orderItemService.createOrderItem(createOrderItemRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualiza cantidad y/o precio de un item de orden existente.
     */
    @Operation(summary = "Actualizar item de orden", description = "Modifica la cantidad o precio unitario de un item; recalcula el lineTotal automáticamente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o datos incorrectos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderItem(@PathVariable Integer id, @RequestBody UpdateOrderItemRequest updateOrderItemRequest) {
        try {
            return ResponseEntity.ok(orderItemService.updateOrderItem(id, updateOrderItemRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
