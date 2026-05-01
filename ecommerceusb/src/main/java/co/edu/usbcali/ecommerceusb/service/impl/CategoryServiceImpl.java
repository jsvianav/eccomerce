package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CategoryResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCategoryRequest;
import co.edu.usbcali.ecommerceusb.mapper.CategoryMapper;
import co.edu.usbcali.ecommerceusb.model.Category;
import co.edu.usbcali.ecommerceusb.repository.CategoryRepository;
import co.edu.usbcali.ecommerceusb.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> getCategories() {
        List<Category> categories = categoryRepository.findAll();

        if (categories.isEmpty()) {
            return List.of();
        }

        return CategoryMapper.modelToCategoryResponseList(categories);
    }

    @Override
    public CategoryResponse getCategoryById(Integer id) throws Exception {

        if (id == null || id <= 0) {
            throw new Exception("Debe ingresar el id para buscar");
        }

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new Exception(
                                String.format("Categoría no encontrada con el id: %d", id)));

        return CategoryMapper.modelToCategoryResponse(category);
    }

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest createCategoryRequest) throws Exception {

        // Validar campo name
        if (Objects.isNull(createCategoryRequest.getName()) ||
                createCategoryRequest.getName().isBlank()) {
            throw new Exception("El campo name no puede estar nulo ni vacío");
        }

        // Resolver categoría padre (opcional)
        Category parent = null;
        if (createCategoryRequest.getParentId() != null) {
            if (createCategoryRequest.getParentId() <= 0) {
                throw new Exception("El campo parentId debe ser mayor a 0");
            }
            parent = categoryRepository.findById(createCategoryRequest.getParentId())
                    .orElseThrow(() -> new Exception("La categoría padre no existe"));
        }

        // Construir y guardar la categoría
        Category category = Category.builder()
                .name(createCategoryRequest.getName().trim())
                .parent(parent)
                .createdAt(OffsetDateTime.now())
                .build();

        Category savedCategory = categoryRepository.save(category);

        return CategoryMapper.modelToCategoryResponse(savedCategory);
    }
}
