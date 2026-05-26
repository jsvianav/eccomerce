package co.edu.usbcali.ecommerceusb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategoryResponse {

    private Integer id;
    private Integer productId;
    private String productName;
    private Integer categoryId;
    private String categoryName;
}
