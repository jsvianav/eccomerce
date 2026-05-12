package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CategoryResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCategoryRequest;
import co.edu.usbcali.ecommerceusb.dto.UpdateCategoryRequest;
import co.edu.usbcali.ecommerceusb.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de categorías de productos.
 * Expone los endpoints CRUD bajo la ruta base /category.
 */
@RestController
@RequestMapping("/category")
@Tag(name = "Category", description = "Operaciones para la gestión de categorías de productos")
public class CategoryController {

    // Inyección del servicio que contiene la lógica de negocio de categorías
    @Autowired
    private CategoryService categoryService;

    /**
     * Retorna la lista completa de categorías registradas.
     */
    @Operation(summary = "Listar todas las categorías", description = "Retorna todas las categorías de productos disponibles")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        return ResponseEntity.ok(categoryService.getCategories());
    }

    /**
     * Busca una categoría por su ID.
     */
    @Operation(summary = "Buscar categoría por ID", description = "Retorna una categoría específica según su identificador")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría encontrada exitosamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o categoría no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(categoryService.getCategoryById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Crea una nueva categoría.
     */
    @Operation(summary = "Crear una categoría", description = "Registra una nueva categoría de productos en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en el request")
    })
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CreateCategoryRequest createCategoryRequest) {
        try {
            return ResponseEntity.ok(categoryService.createCategory(createCategoryRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualiza una categoría existente.
     */
    @Operation(summary = "Actualizar una categoría", description = "Modifica el nombre de una categoría existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o datos incorrectos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Integer id, @RequestBody UpdateCategoryRequest updateCategoryRequest) {
        try {
            return ResponseEntity.ok(categoryService.updateCategory(id, updateCategoryRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
