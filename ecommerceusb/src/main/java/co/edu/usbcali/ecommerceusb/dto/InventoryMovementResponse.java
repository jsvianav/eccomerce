package co.edu.usbcali.ecommerceusb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryMovementResponse {

    private Integer id;
    private Integer productId;
    private Integer orderId;
    private String type;
    private Integer qty;
}
