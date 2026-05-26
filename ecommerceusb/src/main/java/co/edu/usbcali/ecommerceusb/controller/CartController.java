package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CartResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCartRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteCartResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateCartRequest;
import co.edu.usbcali.ecommerceusb.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de carritos de compra.
 * Expone los endpoints CRUD bajo la ruta base /cart.
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /** Retorna la lista completa de carritos registrados. */
    @GetMapping
    public ResponseEntity<List<CartResponse>> getCarts() {
        return ResponseEntity.ok(cartService.getCarts());
    }

    /** Busca un carrito por su ID. */
    @GetMapping("/{id}")
    public ResponseEntity<CartResponse> getCartById(@PathVariable Integer id) {
        return ResponseEntity.ok(cartService.getCartById(id));
    }

    /** Crea un nuevo carrito. Retorna 201 Created. */
    @PostMapping
    public ResponseEntity<CartResponse> createCart(@RequestBody CreateCartRequest createCartRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.createCart(createCartRequest));
    }

    /** Actualiza un carrito existente identificado por su ID. */
    @PutMapping("/{id}")
    public ResponseEntity<CartResponse> updateCart(@PathVariable Integer id, @RequestBody UpdateCartRequest updateCartRequest) {
        return ResponseEntity.ok(cartService.updateCart(id, updateCartRequest));
    }

    /** Elimina un carrito por su ID. */
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteCartResponse> deleteCart(@PathVariable Integer id) {
        return ResponseEntity.ok(cartService.deleteCart(id));
    }
}
