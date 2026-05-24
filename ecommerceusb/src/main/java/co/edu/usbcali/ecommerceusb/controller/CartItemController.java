package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CartItemResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCartItemRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteCartItemResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateCartItemRequest;
import co.edu.usbcali.ecommerceusb.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de items dentro de un carrito de compra.
 * Expone los endpoints CRUD bajo la ruta base /cartItem.
 */
@RestController
@RequestMapping("/cartItem")
public class CartItemController {

    // Inyección del servicio que contiene la lógica de negocio de items de carrito
    @Autowired
    private CartItemService cartItemService;

    /**
     * Retorna la lista completa de items de carrito registrados.
     * No requiere parámetros. Siempre retorna 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCartItems() {
        // Delega al servicio y envuelve el resultado en un 200 OK
        return ResponseEntity.ok(cartItemService.getCartItems());
    }

    /**
     * Busca un item de carrito por su ID.
     * Retorna 200 OK si existe, o 400 Bad Request si el ID es inválido o no se encuentra.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCartItemById(@PathVariable Integer id) {
        try {
            // Intenta buscar el item; retorna 200 si existe
            return ResponseEntity.ok(cartItemService.getCartItemById(id));
        } catch (Exception e) {
            // Si el servicio lanza una excepción, retorna 400 con el mensaje de error
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Agrega un nuevo producto a un carrito existente.
     * Retorna 200 OK con el item creado, o 400 si los datos son inválidos
     * o el producto ya estaba en ese carrito.
     */
    @PostMapping
    public ResponseEntity<?> createCartItem(@RequestBody CreateCartItemRequest createCartItemRequest) {
        try {
            // Pasa el request al servicio para que lo valide y persista
            return ResponseEntity.ok(cartItemService.createCartItem(createCartItemRequest));
        } catch (Exception e) {
            // Retorna 400 con el mensaje de validación si algo falla
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualiza la cantidad o el producto de un item de carrito existente.
     * Solo modifica los campos enviados en el body.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCartItem(@PathVariable Integer id, @RequestBody UpdateCartItemRequest updateCartItemRequest) {
        try {
            // Pasa el id y el request al servicio para que actualice solo los campos recibidos
            return ResponseEntity.ok(cartItemService.updateCartItem(id, updateCartItemRequest));
        } catch (Exception e) {
            // Retorna 400 con el mensaje de error si la operación falla
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteCartItemResponse> deleteCartItem(
            @PathVariable Integer id) throws Exception {
        return new ResponseEntity<>(cartItemService.deleteCartItem(id), HttpStatus.OK);
    }
}
