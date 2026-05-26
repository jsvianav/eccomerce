package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreateProductCategoryRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteProductCategoryResponse;
import co.edu.usbcali.ecommerceusb.dto.ProductCategoryResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateProductCategoryRequest;
import co.edu.usbcali.ecommerceusb.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de asignaciones producto-categoría.
 * Permite asociar un producto a una o más categorías.
 * Expone los endpoints CRUD bajo la ruta base /productCategory.
 */
@RestController
@RequestMapping("/productCategory")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    /** Retorna la lista completa de asignaciones producto-categoría registradas. */
    @GetMapping
    public ResponseEntity<List<ProductCategoryResponse>> getProductCategories() {
        return ResponseEntity.ok(productCategoryService.getProductCategories());
    }

    /** Busca una asignación producto-categoría por su ID. */
    @GetMapping("/{id}")
    public ResponseEntity<ProductCategoryResponse> getProductCategoryById(@PathVariable Integer id) {
        return ResponseEntity.ok(productCategoryService.getProductCategoryById(id));
    }

    /** Asigna un producto a una categoría. Retorna 201 Created. */
    @PostMapping
    public ResponseEntity<ProductCategoryResponse> createProductCategory(@RequestBody CreateProductCategoryRequest createProductCategoryRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productCategoryService.createProductCategory(createProductCategoryRequest));
    }

    /** Actualiza la asignación (cambia el producto, la categoría, o ambos). */
    @PutMapping("/{id}")
    public ResponseEntity<ProductCategoryResponse> updateProductCategory(@PathVariable Integer id, @RequestBody UpdateProductCategoryRequest updateProductCategoryRequest) {
        return ResponseEntity.ok(productCategoryService.updateProductCategory(id, updateProductCategoryRequest));
    }

    /** Elimina una asignación producto-categoría por su ID. */
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteProductCategoryResponse> deleteProductCategory(@PathVariable Integer id) {
        return ResponseEntity.ok(productCategoryService.deleteProductCategory(id));
    }
}
