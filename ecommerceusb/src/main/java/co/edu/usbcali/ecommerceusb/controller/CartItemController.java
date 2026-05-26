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

    @Autowired
    private CartItemService cartItemService;

    /** Retorna la lista completa de items de carrito registrados. */
    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCartItems() {
        return ResponseEntity.ok(cartItemService.getCartItems());
    }

    /** Busca un item de carrito por su ID. */
    @GetMapping("/{id}")
    public ResponseEntity<CartItemResponse> getCartItemById(@PathVariable Integer id) {
        return ResponseEntity.ok(cartItemService.getCartItemById(id));
    }

    /** Agrega un nuevo producto a un carrito existente. Retorna 201 Created. */
    @PostMapping
    public ResponseEntity<CartItemResponse> createCartItem(@RequestBody CreateCartItemRequest createCartItemRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemService.createCartItem(createCartItemRequest));
    }

    /** Actualiza la cantidad o el producto de un item de carrito existente. */
    @PutMapping("/{id}")
    public ResponseEntity<CartItemResponse> updateCartItem(@PathVariable Integer id, @RequestBody UpdateCartItemRequest updateCartItemRequest) {
        return ResponseEntity.ok(cartItemService.updateCartItem(id, updateCartItemRequest));
    }

    /** Elimina un item de carrito por su ID. */
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteCartItemResponse> deleteCartItem(@PathVariable Integer id) {
        return ResponseEntity.ok(cartItemService.deleteCartItem(id));
    }
}
