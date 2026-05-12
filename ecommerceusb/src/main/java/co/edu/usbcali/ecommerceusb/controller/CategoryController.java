package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CategoryResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCategoryRequest;
import co.edu.usbcali.ecommerceusb.dto.UpdateCategoryRequest;
import co.edu.usbcali.ecommerceusb.service.CategoryService;
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
public class CategoryController {

    // Inyección del servicio que contiene la lógica de negocio de categorías
    @Autowired
    private CategoryService categoryService;

    /**
     * Retorna la lista completa de categorías registradas.
     * No requiere parámetros. Siempre retorna 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        // Delega al servicio y envuelve el resultado en un 200 OK
        return ResponseEntity.ok(categoryService.getCategories());
    }

    /**
     * Busca una categoría por su ID.
     * Retorna 200 OK si existe, o 400 Bad Request si el ID es inválido o no se encuentra.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Integer id) {
        try {
            // Intenta buscar la categoría; retorna 200 si existe
            return ResponseEntity.ok(categoryService.getCategoryById(id));
        } catch (Exception e) {
            // Si el servicio lanza una excepción, retorna 400 con el mensaje de error
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Crea una nueva categoría.
     * Retorna 200 OK con la categoría creada, o 400 si el nombre está vacío.
     */
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CreateCategoryRequest createCategoryRequest) {
        try {
            // Pasa el request al servicio para que lo valide y persista
            return ResponseEntity.ok(categoryService.createCategory(createCategoryRequest));
        } catch (Exception e) {
            // Retorna 400 con el mensaje de validación si algo falla
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualiza el nombre de una categoría existente.
     * Solo modifica el campo enviado en el body.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Integer id, @RequestBody UpdateCategoryRequest updateCategoryRequest) {
        try {
            // Pasa el id y el request al servicio para que actualice solo los campos recibidos
            return ResponseEntity.ok(categoryService.updateCategory(id, updateCategoryRequest));
        } catch (Exception e) {
            // Retorna 400 con el mensaje de error si la operación falla
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
