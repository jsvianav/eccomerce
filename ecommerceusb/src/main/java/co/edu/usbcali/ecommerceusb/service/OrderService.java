package co.edu.usbcali.ecommerceusb.service;

import co.edu.usbcali.ecommerceusb.dto.CreateOrderRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteOrderResponse;
import co.edu.usbcali.ecommerceusb.dto.OrderResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateOrderRequest;

import java.util.List;

public interface OrderService {
    List<OrderResponse> getOrders();
    OrderResponse getOrderById(Integer id) throws Exception;
    OrderResponse createOrder(CreateOrderRequest createOrderRequest) throws Exception;
    OrderResponse updateOrder(Integer id, UpdateOrderRequest updateOrderRequest) throws Exception;
    DeleteOrderResponse deleteOrder(Integer id) throws Exception;
}
