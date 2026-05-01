package co.edu.usbcali.ecommerceusb.mapper;

import co.edu.usbcali.ecommerceusb.dto.ProductResponse;
import co.edu.usbcali.ecommerceusb.model.Product;

import java.util.List;

public class ProductMapper {

    public static ProductResponse modelToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .available(product.getAvailable())
                .build();
    }

    public static List<ProductResponse> modelToProductResponseList(List<Product> products) {
        return products.stream().map(ProductMapper::modelToProductResponse).toList();
    }
}
