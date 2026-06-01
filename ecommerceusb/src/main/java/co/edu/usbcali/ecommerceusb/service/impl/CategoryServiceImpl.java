package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CategoryResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCategoryRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteCategoryResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateCategoryRequest;
import co.edu.usbcali.ecommerceusb.exception.BadRequestException;
import co.edu.usbcali.ecommerceusb.exception.NotFoundException;
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
        if (categories.isEmpty()) return List.of();
        return CategoryMapper.modelToCategoryResponseList(categories);
    }

    /**
     * Busca y retorna una categoría específica por su ID.
     * Lanza una excepción si el ID es inválido o si la categoría no existe.
     */
    @Override
    public CategoryResponse getCategoryById(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar el id para buscar");
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Categoría no encontrada con el id: %d", id)));
        return CategoryMapper.modelToCategoryResponse(category);
    }

    /**
     * Crea una nueva categoría en la base de datos.
     * Valida que el request no sea nulo y que el campo name no esté vacío.
     * Si se envía parentId, busca y asigna la categoría padre.
     */
    @Override
    public CategoryResponse createCategory(CreateCategoryRequest req) {
        if (Objects.isNull(req))
            throw new BadRequestException("El objeto createCategoryRequest no puede ser nulo");
        if (Objects.isNull(req.getName()) || req.getName().isBlank())
            throw new BadRequestException("El campo name no puede ser nulo ni vacío");

        Category parent = null;
        if (req.getParentId() != null) {
            parent = categoryRepository.findById(req.getParentId())
                    .orElseThrow(() -> new NotFoundException(
                            String.format("Categoría padre no encontrada con el id: %d", req.getParentId())));
        }

        Category category = CategoryMapper.createCategoryRequestToCategory(req, parent);
        categoryRepository.save(category);
        return CategoryMapper.modelToCategoryResponse(category);
    }

    /**
     * Actualiza nombre y/o categoría padre de una categoría existente.
     * parentId null en el request elimina la relación padre (sin categoría padre).
     * parentId con valor válido asigna esa categoría como padre.
     */
    @Override
    public CategoryResponse updateCategory(Integer id, UpdateCategoryRequest req) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Categoría no encontrada con el id: %d", id)));

        if (req.getName() != null && !req.getName().isBlank()) {
            category.setName(req.getName());
        }

        // parentId presente en el request: null → quita padre, número → asigna padre
        if (req.getParentId() != null) {
            if (req.getParentId().equals(id)) {
                throw new BadRequestException("Una categoría no puede ser su propio padre");
            }
            Category parent = categoryRepository.findById(req.getParentId())
                    .orElseThrow(() -> new NotFoundException(
                            String.format("Categoría padre no encontrada con el id: %d", req.getParentId())));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        categoryRepository.save(category);
        return CategoryMapper.modelToCategoryResponse(category);
    }

    /**
     * Elimina una categoría existente por su ID.
     * Lanza excepción si el ID es inválido o si la categoría no existe.
     */
    @Override
    public DeleteCategoryResponse deleteCategory(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Categoría no encontrada con el id: %d", id)));
        categoryRepository.delete(category);
        return new DeleteCategoryResponse("Categoría con id " + id + " eliminada correctamente");
    }
}
