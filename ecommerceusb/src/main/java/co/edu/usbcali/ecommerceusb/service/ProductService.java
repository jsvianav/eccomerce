package co.edu.usbcali.ecommerceusb.service;

import co.edu.usbcali.ecommerceusb.dto.CreateProductRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteProductResponse;
import co.edu.usbcali.ecommerceusb.dto.ProductResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateProductRequest;
import java.util.List;

public interface ProductService {
    List<ProductResponse> getProducts();
    ProductResponse getProductById(Integer id);
    ProductResponse createProduct(CreateProductRequest createProductRequest);
    ProductResponse updateProduct(Integer id, UpdateProductRequest updateProductRequest);
    DeleteProductResponse deleteProduct(Integer id);
}