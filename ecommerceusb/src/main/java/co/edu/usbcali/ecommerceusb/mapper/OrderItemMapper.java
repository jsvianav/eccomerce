package co.edu.usbcali.ecommerceusb.mapper;

import co.edu.usbcali.ecommerceusb.dto.OrderItemResponse;
import co.edu.usbcali.ecommerceusb.model.OrderItem;

import java.util.List;

public class OrderItemMapper {

    public static OrderItemResponse modelToOrderItemResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrder() != null ? orderItem.getOrder().getId() : null)
                .productId(orderItem.getProduct() != null ? orderItem.getProduct().getId() : null)
                .quantity(orderItem.getQuantity())
                .unitPriceSnapshot(orderItem.getUnitPriceSnapshot())
                .lineTotal(orderItem.getLineTotal())
                .build();
    }

    public static List<OrderItemResponse> modelToOrderItemResponseList(List<OrderItem> orderItems) {
        return orderItems.stream().map(OrderItemMapper::modelToOrderItemResponse).toList();
    }
}
