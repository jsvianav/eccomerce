package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreateProductRequest;
import co.edu.usbcali.ecommerceusb.dto.ProductResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateProductRequest;
import co.edu.usbcali.ecommerceusb.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de productos del catálogo.
 * Expone los endpoints CRUD bajo la ruta base /product.
 */
@RestController
@RequestMapping("/product")
@Tag(name = "Product", description = "Operaciones para la gestión del catálogo de productos")
public class ProductController {

    // Inyección del servicio que contiene la lógica de negocio de productos
    @Autowired
    private ProductService productService;

    /**
     * Retorna la lista completa de productos registrados.
     * No requiere parámetros. Siempre retorna 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts() {
        // Delega al servicio y envuelve el resultado en un 200 OK
        return ResponseEntity.ok(productService.getProducts());
    }

    /**
     * Busca un producto por su ID.
     * Retorna 200 OK si existe, o 400 Bad Request si el ID es inválido o no se encuentra.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Integer id) {
        try {
            // Intenta buscar el producto; retorna 200 si existe
            return ResponseEntity.ok(productService.getProductById(id));
        } catch (Exception e) {
            // Si el servicio lanza una excepción, retorna 400 con el mensaje de error
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Crea un nuevo producto en el catálogo.
     * Retorna 200 OK con el producto creado, o 400 si los datos son inválidos.
     */
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody CreateProductRequest createProductRequest) {
        try {
            // Pasa el request al servicio para que lo valide y persista
            return ResponseEntity.ok(productService.createProduct(createProductRequest));
        } catch (Exception e) {
            // Retorna 400 con el mensaje de validación si algo falla
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualiza los datos de un producto existente.
     * Solo modifica los campos enviados en el body (name, description, price, available).
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Integer id, @RequestBody UpdateProductRequest updateProductRequest) {
        try {
            // Pasa el id y el request al servicio para que actualice solo los campos recibidos
            return ResponseEntity.ok(productService.updateProduct(id, updateProductRequest));
        } catch (Exception e) {
            // Retorna 400 con el mensaje de error si la operación falla
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
