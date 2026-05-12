package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CartItemResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCartItemRequest;
import co.edu.usbcali.ecommerceusb.dto.UpdateCartItemRequest;
import co.edu.usbcali.ecommerceusb.service.CartItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de items dentro de un carrito de compra.
 * Expone los endpoints CRUD bajo la ruta base /cartItem.
 */
@RestController
@RequestMapping("/cartItem")
@Tag(name = "CartItem", description = "Operaciones para la gestión de productos dentro de un carrito")
public class CartItemController {

    // Inyección del servicio que contiene la lógica de negocio de items de carrito
    @Autowired
    private CartItemService cartItemService;

    /**
     * Retorna la lista completa de items de carrito registrados.
     */
    @Operation(summary = "Listar todos los items de carrito", description = "Retorna todos los productos agregados en carritos de compra")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCartItems() {
        return ResponseEntity.ok(cartItemService.getCartItems());
    }

    /**
     * Busca un item de carrito por su ID.
     */
    @Operation(summary = "Buscar item de carrito por ID", description = "Retorna un item específico según su identificador")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item encontrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o item no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getCartItemById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(cartItemService.getCartItemById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Agrega un nuevo producto a un carrito existente.
     */
    @Operation(summary = "Agregar item al carrito", description = "Agrega un producto a un carrito de compra existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item agregado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o producto ya existe en el carrito")
    })
    @PostMapping
    public ResponseEntity<?> createCartItem(@RequestBody CreateCartItemRequest createCartItemRequest) {
        try {
            return ResponseEntity.ok(cartItemService.createCartItem(createCartItemRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualiza la cantidad o el producto de un item de carrito existente.
     */
    @Operation(summary = "Actualizar item de carrito", description = "Modifica la cantidad o el producto de un item dentro de un carrito")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o datos incorrectos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCartItem(@PathVariable Integer id, @RequestBody UpdateCartItemRequest updateCartItemRequest) {
        try {
            return ResponseEntity.ok(cartItemService.updateCartItem(id, updateCartItemRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
