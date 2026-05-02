package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreateProductRequest;
import co.edu.usbcali.ecommerceusb.dto.ProductResponse;
import co.edu.usbcali.ecommerceusb.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts() {
        return ResponseEntity.ok(productService.getProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(productService.getProductById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @RequestBody CreateProductRequest createProductRequest) {
        try {
            return ResponseEntity.ok(productService.createProduct(createProductRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
