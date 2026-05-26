package co.edu.usbcali.ecommerceusb.mapper;

import co.edu.usbcali.ecommerceusb.dto.ProductCategoryResponse;
import co.edu.usbcali.ecommerceusb.model.ProductCategory;

import java.util.List;

public class ProductCategoryMapper {

    public static ProductCategoryResponse modelToProductCategoryResponse(ProductCategory pc) {
        return ProductCategoryResponse.builder()
                .id(pc.getId())
                .productId(pc.getProduct().getId())
                .productName(pc.getProduct().getName())
                .categoryId(pc.getCategory().getId())
                .categoryName(pc.getCategory().getName())
                .build();
    }

    public static List<ProductCategoryResponse> modelToProductCategoryResponseList(List<ProductCategory> list) {
        return list.stream().map(ProductCategoryMapper::modelToProductCategoryResponse).toList();
    }
}
