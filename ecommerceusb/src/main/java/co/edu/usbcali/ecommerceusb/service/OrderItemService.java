package co.edu.usbcali.ecommerceusb.service;

import co.edu.usbcali.ecommerceusb.dto.CreateOrderItemRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteOrderItemResponse;
import co.edu.usbcali.ecommerceusb.dto.OrderItemResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateOrderItemRequest;
import java.util.List;

public interface OrderItemService {
    List<OrderItemResponse> getOrderItems();
    OrderItemResponse getOrderItemById(Integer id);
    OrderItemResponse createOrderItem(CreateOrderItemRequest createOrderItemRequest);
    OrderItemResponse updateOrderItem(Integer id, UpdateOrderItemRequest updateOrderItemRequest);
    DeleteOrderItemResponse deleteOrderItem(Integer id);
}
