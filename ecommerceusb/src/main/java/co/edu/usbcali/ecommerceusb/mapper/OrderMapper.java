package co.edu.usbcali.ecommerceusb.mapper;

import co.edu.usbcali.ecommerceusb.dto.OrderResponse;
import co.edu.usbcali.ecommerceusb.model.Order;

import java.util.List;

public class OrderMapper {

    public static OrderResponse modelToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .totalAmount(order.getTotalAmount())
                .currency(order.getCurrency())
                .build();
    }

    public static List<OrderResponse> modelToOrderResponseList(List<Order> orders) {
        return orders.stream().map(OrderMapper::modelToOrderResponse).toList();
    }
}
