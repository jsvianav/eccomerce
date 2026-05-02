package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CartResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCartRequest;
import co.edu.usbcali.ecommerceusb.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartResponse>> getCarts() {
        return ResponseEntity.ok(cartService.getCarts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCartById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(cartService.getCartById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createCart(
            @RequestBody CreateCartRequest createCartRequest) {
        try {
            return ResponseEntity.ok(cartService.createCart(createCartRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
