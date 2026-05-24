package co.edu.usbcali.ecommerceusb.service;

import co.edu.usbcali.ecommerceusb.dto.CreateOrderItemRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteOrderItemResponse;
import co.edu.usbcali.ecommerceusb.dto.OrderItemResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateOrderItemRequest;

import java.util.List;

public interface OrderItemService {
    List<OrderItemResponse> getOrderItems();
    OrderItemResponse getOrderItemById(Integer id) throws Exception;
    OrderItemResponse createOrderItem(CreateOrderItemRequest createOrderItemRequest) throws Exception;
    OrderItemResponse updateOrderItem(Integer id, UpdateOrderItemRequest updateOrderItemRequest) throws Exception;
    DeleteOrderItemResponse deleteOrderItem(Integer id) throws Exception;
}
