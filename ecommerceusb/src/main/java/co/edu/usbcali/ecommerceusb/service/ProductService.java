package co.edu.usbcali.ecommerceusb.service;

import co.edu.usbcali.ecommerceusb.dto.CreateProductRequest;
import co.edu.usbcali.ecommerceusb.dto.ProductResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateProductRequest;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getProducts();
    ProductResponse getProductById(Integer id) throws Exception;
    ProductResponse createProduct(CreateProductRequest createProductRequest) throws Exception;
    ProductResponse updateProduct(Integer id, UpdateProductRequest updateProductRequest) throws Exception;
}
