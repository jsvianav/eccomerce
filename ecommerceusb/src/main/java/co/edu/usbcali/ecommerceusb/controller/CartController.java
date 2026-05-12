package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CartResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCartRequest;
import co.edu.usbcali.ecommerceusb.dto.UpdateCartRequest;
import co.edu.usbcali.ecommerceusb.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de carritos de compra.
 * Expone los endpoints CRUD bajo la ruta base /cart.
 */
@RestController
@RequestMapping("/cart")
@Tag(name = "Cart", description = "Operaciones para la gestión de carritos de compra")
public class CartController {

    // Inyección del servicio que contiene la lógica de negocio de carritos
    @Autowired
    private CartService cartService;

    /**
     * Retorna la lista completa de carritos registrados.
     * No requiere parámetros. Siempre retorna 200 OK (lista vacía si no hay datos).
     */
    @Operation(summary = "Listar todos los carritos", description = "Retorna la lista de todos los carritos de compra registrados en el sistema")
    @ApiResponse(responseCode = "200", description = "Lista de carritos obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<CartResponse>> getCarts() {
        // Delega al servicio y envuelve el resultado en un 200 OK
        return ResponseEntity.ok(cartService.getCarts());
    }

    /**
     * Busca un carrito por su ID.
     * Retorna 200 OK con el carrito si existe, o 400 Bad Request si el ID es inválido o no se encuentra.
     */
    @Operation(summary = "Buscar carrito por ID", description = "Retorna un carrito específico según su identificador")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrito encontrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o carrito no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getCartById(@PathVariable Integer id) {
        try {
            // Intenta buscar el carrito; retorna 200 si existe
            return ResponseEntity.ok(cartService.getCartById(id));
        } catch (Exception e) {
            // Si el servicio lanza una excepción, retorna 400 con el mensaje de error
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Crea un nuevo carrito.
     * Recibe el objeto CreateCartRequest en el body. Retorna 200 OK con el carrito creado,
     * o 400 Bad Request si los datos son inválidos.
     */
    @Operation(summary = "Crear un carrito", description = "Crea un nuevo carrito de compra para un usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrito creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en el request")
    })
    @PostMapping
    public ResponseEntity<?> createCart(@RequestBody CreateCartRequest createCartRequest) {
        try {
            // Pasa el request al servicio para que lo valide y persista
            return ResponseEntity.ok(cartService.createCart(createCartRequest));
        } catch (Exception e) {
            // Retorna 400 con el mensaje de validación si algo falla
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualiza un carrito existente identificado por su ID.
     * Solo modifica los campos enviados en el body. Retorna 200 OK con el carrito actualizado,
     * o 400 Bad Request si el ID o los datos son inválidos.
     */
    @Operation(summary = "Actualizar un carrito", description = "Actualiza el estado u otros campos de un carrito existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrito actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o datos incorrectos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCart(@PathVariable Integer id, @RequestBody UpdateCartRequest updateCartRequest) {
        try {
            // Pasa el id y el request al servicio para que actualice solo los campos recibidos
            return ResponseEntity.ok(cartService.updateCart(id, updateCartRequest));
        } catch (Exception e) {
            // Retorna 400 con el mensaje de error si la operación falla
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
