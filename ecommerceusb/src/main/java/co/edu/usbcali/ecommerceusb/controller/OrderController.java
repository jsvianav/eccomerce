package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreateOrderRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteOrderResponse;
import co.edu.usbcali.ecommerceusb.dto.OrderResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateOrderRequest;
import co.edu.usbcali.ecommerceusb.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de órdenes de compra.
 * Expone los endpoints CRUD bajo la ruta base /order.
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    // Inyección del servicio que contiene la lógica de negocio de órdenes
    @Autowired
    private OrderService orderService;

    /**
     * Retorna la lista completa de órdenes registradas.
     * No requiere parámetros. Siempre retorna 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders() {
        // Delega al servicio y envuelve el resultado en un 200 OK
        return ResponseEntity.ok(orderService.getOrders());
    }

    /**
     * Busca una orden por su ID.
     * Retorna 200 OK si existe, o 400 Bad Request si el ID es inválido o no se encuentra.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Integer id) {
        try {
            // Intenta buscar la orden; retorna 200 si existe
            return ResponseEntity.ok(orderService.getOrderById(id));
        } catch (Exception e) {
            // Si el servicio lanza una excepción, retorna 400 con el mensaje de error
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Crea una nueva orden de compra.
     * Retorna 200 OK con la orden creada, o 400 si los datos son inválidos.
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        try {
            // Pasa el request al servicio para que lo valide y persista
            return ResponseEntity.ok(orderService.createOrder(createOrderRequest));
        } catch (Exception e) {
            // Retorna 400 con el mensaje de validación si algo falla
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualiza una orden existente identificada por su ID.
     * Solo modifica los campos enviados en el body (status, totalAmount, currency).
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Integer id, @RequestBody UpdateOrderRequest updateOrderRequest) {
        try {
            // Pasa el id y el request al servicio para que actualice solo los campos recibidos
            return ResponseEntity.ok(orderService.updateOrder(id, updateOrderRequest));
        } catch (Exception e) {
            // Retorna 400 con el mensaje de error si la operación falla
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteOrderResponse> deleteOrder(
            @PathVariable Integer id) throws Exception {
        return new ResponseEntity<>(orderService.deleteOrder(id), HttpStatus.OK);
    }
}
