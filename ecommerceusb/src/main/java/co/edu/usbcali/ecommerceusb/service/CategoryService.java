package co.edu.usbcali.ecommerceusb.service;

import co.edu.usbcali.ecommerceusb.dto.CategoryResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCategoryRequest;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getCategories();
    CategoryResponse getCategoryById(Integer id) throws Exception;
    CategoryResponse createCategory(CreateCategoryRequest createCategoryRequest) throws Exception;
    CategoryResponse updateCategory(Integer id, CreateCategoryRequest createCategoryRequest) throws Exception;
}
