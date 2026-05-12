package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreateOrderRequest;
import co.edu.usbcali.ecommerceusb.dto.OrderResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateOrderRequest;
import co.edu.usbcali.ecommerceusb.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de órdenes de compra.
 * Expone los endpoints CRUD bajo la ruta base /order.
 */
@RestController
@RequestMapping("/order")
@Tag(name = "Order", description = "Operaciones para la gestión de órdenes de compra")
public class OrderController {

    // Inyección del servicio que contiene la lógica de negocio de órdenes
    @Autowired
    private OrderService orderService;

    /**
     * Retorna la lista completa de órdenes registradas.
     */
    @Operation(summary = "Listar todas las órdenes", description = "Retorna todas las órdenes de compra registradas en el sistema")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders() {
        return ResponseEntity.ok(orderService.getOrders());
    }

    /**
     * Busca una orden por su ID.
     */
    @Operation(summary = "Buscar orden por ID", description = "Retorna una orden específica según su identificador")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orden encontrada exitosamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o la orden no existe")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(orderService.getOrderById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Crea una nueva orden de compra.
     */
    @Operation(summary = "Crear una orden", description = "Registra una nueva orden de compra asociada a un usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orden creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en el request")
    })
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        try {
            return ResponseEntity.ok(orderService.createOrder(createOrderRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualiza una orden existente.
     */
    @Operation(summary = "Actualizar una orden", description = "Modifica el estado, total o moneda de una orden existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orden actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o datos incorrectos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Integer id, @RequestBody UpdateOrderRequest updateOrderRequest) {
        try {
            return ResponseEntity.ok(orderService.updateOrder(id, updateOrderRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
