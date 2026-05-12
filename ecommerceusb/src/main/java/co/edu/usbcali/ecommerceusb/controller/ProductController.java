package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreateProductRequest;
import co.edu.usbcali.ecommerceusb.dto.ProductResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateProductRequest;
import co.edu.usbcali.ecommerceusb.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
     */
    @Operation(summary = "Listar todos los productos", description = "Retorna todos los productos disponibles en el catálogo")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts() {
        return ResponseEntity.ok(productService.getProducts());
    }

    /**
     * Busca un producto por su ID.
     */
    @Operation(summary = "Buscar producto por ID", description = "Retorna un producto específico según su identificador")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(productService.getProductById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Crea un nuevo producto en el catálogo.
     */
    @Operation(summary = "Crear un producto", description = "Registra un nuevo producto en el catálogo del ecommerce")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en el request")
    })
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody CreateProductRequest createProductRequest) {
        try {
            return ResponseEntity.ok(productService.createProduct(createProductRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualiza los datos de un producto existente.
     */
    @Operation(summary = "Actualizar un producto", description = "Modifica los campos de un producto existente en el catálogo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o datos incorrectos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Integer id, @RequestBody UpdateProductRequest updateProductRequest) {
        try {
            return ResponseEntity.ok(productService.updateProduct(id, updateProductRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
