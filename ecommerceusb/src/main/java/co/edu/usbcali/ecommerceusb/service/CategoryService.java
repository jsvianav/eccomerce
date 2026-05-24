package co.edu.usbcali.ecommerceusb.service;

import co.edu.usbcali.ecommerceusb.dto.CategoryResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCategoryRequest;
import co.edu.usbcali.ecommerceusb.dto.UpdateCategoryRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteCategoryResponse;
import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getCategories();
    CategoryResponse getCategoryById(Integer id) throws Exception;
    CategoryResponse createCategory(CreateCategoryRequest createCategoryRequest) throws Exception;
    CategoryResponse updateCategory(Integer id, UpdateCategoryRequest updateCategoryRequest) throws Exception;
    DeleteCategoryResponse deleteCategory(Integer id) throws Exception;
}
