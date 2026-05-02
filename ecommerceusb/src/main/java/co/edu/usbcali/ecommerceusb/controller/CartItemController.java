package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CartItemResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCartItemRequest;
import co.edu.usbcali.ecommerceusb.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cartItem")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCartItems() {
        return ResponseEntity.ok(cartItemService.getCartItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCartItemById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(cartItemService.getCartItemById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createCartItem(
            @RequestBody CreateCartItemRequest createCartItemRequest) {
        try {
            return ResponseEntity.ok(cartItemService.createCartItem(createCartItemRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
