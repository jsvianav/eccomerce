package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CategoryResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCategoryRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteCategoryResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateCategoryRequest;
import co.edu.usbcali.ecommerceusb.mapper.CategoryMapper;
import co.edu.usbcali.ecommerceusb.model.Category;
import co.edu.usbcali.ecommerceusb.repository.CategoryRepository;
import co.edu.usbcali.ecommerceusb.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Retorna la lista de todas las categorías registradas en la base de datos.
     * Si no existe ninguna categoría, retorna una lista vacía.
     */
    @Override
    public List<CategoryResponse> getCategories() {
        List<Category> categories = categoryRepository.findAll();
        // Si no hay categorías, retorna lista vacía en lugar de null
        if (categories.isEmpty()) return List.of();
        // Convierte la lista de entidades al formato de respuesta
        return CategoryMapper.modelToCategoryResponseList(categories);
    }

    /**
     * Busca y retorna una categoría específica por su ID.
     * Lanza una excepción si el ID es inválido o si la categoría no existe.
     */
    @Override
    public CategoryResponse getCategoryById(Integer id) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar el id para buscar");
        // Busca la categoría; lanza excepción si no se encuentra
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Categoría no encontrada con el id: %d", id)));
        // Convierte la entidad al objeto de respuesta y lo retorna
        return CategoryMapper.modelToCategoryResponse(category);
    }

    /**
     * Crea una nueva categoría en la base de datos.
     * Valida que el request no sea nulo y que el campo name no esté vacío.
     */
    @Override
    public CategoryResponse createCategory(CreateCategoryRequest req) throws Exception {
        // Valida que el objeto request no sea nulo
        if (Objects.isNull(req))
            throw new Exception("El objeto createCategoryRequest no puede ser nulo");
        // Valida que el campo name no esté vacío ni nulo
        if (Objects.isNull(req.getName()) || req.getName().isBlank())
            throw new Exception("El campo name no puede ser nulo ni vacío");
        // Usa el mapper para convertir el request a la entidad Category
        Category category = CategoryMapper.createCategoryRequestToCategory(req);
        // Guarda la categoría en la base de datos
        categoryRepository.save(category);
        // Retorna la respuesta mapeada de la categoría creada
        return CategoryMapper.modelToCategoryResponse(category);
    }

    /**
     * Actualiza el nombre de una categoría existente identificada por su ID.
     * Solo modifica el nombre si viene en el request; lanza excepción si
     * el ID es inválido o la categoría no existe.
     */
    @Override
    public CategoryResponse updateCategory(Integer id, UpdateCategoryRequest req) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        // Busca la categoría; lanza excepción si no se encuentra
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Categoría no encontrada con el id: %d", id)));
        // Actualiza el nombre solo si viene en el request y no está vacío
        if (req.getName() != null && !req.getName().isBlank()) category.setName(req.getName());
        // Guarda los cambios en la base de datos
        categoryRepository.save(category);
        // Retorna la respuesta mapeada de la categoría actualizada
        return CategoryMapper.modelToCategoryResponse(category);
    }
    /**
     * Elimina una categoría existente por su ID.
     * Lanza excepción si el ID es inválido o si la categoría no existe.
     */
    @Override
    public DeleteCategoryResponse deleteCategory(Integer id) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        // Busca la categoría; lanza excepción si no se encuentra
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Categoría no encontrada con el id: %d", id)));
        // Elimina el registro de la base de datos
        categoryRepository.delete(category);
        // Retorna la respuesta con mensaje de confirmación
        return new DeleteCategoryResponse("Categoría con id " + id + " eliminada correctamente");
    }
}
