package co.edu.usbcali.ecommerceusb.mapper;

import co.edu.usbcali.ecommerceusb.dto.CategoryResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCategoryRequest;
import co.edu.usbcali.ecommerceusb.model.Category;

import java.time.OffsetDateTime;
import java.util.List;

public class CategoryMapper {

    public static CategoryResponse modelToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .build();
    }

    public static List<CategoryResponse> modelToCategoryResponseList(List<Category> categories) {
        return categories.stream().map(CategoryMapper::modelToCategoryResponse).toList();
    }

    public static Category createCategoryRequestToCategory(CreateCategoryRequest req, Category parent) {
        return Category.builder()
                .name(req.getName())
                .parent(parent)
                .createdAt(OffsetDateTime.now())
                .build();
    }
}
