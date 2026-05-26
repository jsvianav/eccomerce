package co.edu.usbcali.ecommerceusb.service;

import co.edu.usbcali.ecommerceusb.dto.CreateProductCategoryRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteProductCategoryResponse;
import co.edu.usbcali.ecommerceusb.dto.ProductCategoryResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateProductCategoryRequest;

import java.util.List;

public interface ProductCategoryService {
    List<ProductCategoryResponse> getProductCategories();
    ProductCategoryResponse getProductCategoryById(Integer id);
    ProductCategoryResponse createProductCategory(CreateProductCategoryRequest createProductCategoryRequest);
    ProductCategoryResponse updateProductCategory(Integer id, UpdateProductCategoryRequest updateProductCategoryRequest);
    DeleteProductCategoryResponse deleteProductCategory(Integer id);
}
