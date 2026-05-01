package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateOrderRequest;
import co.edu.usbcali.ecommerceusb.dto.OrderResponse;
import co.edu.usbcali.ecommerceusb.mapper.OrderMapper;
import co.edu.usbcali.ecommerceusb.model.Order;
import co.edu.usbcali.ecommerceusb.model.Order.OrderStatus;
import co.edu.usbcali.ecommerceusb.model.User;
import co.edu.usbcali.ecommerceusb.repository.OrderRepository;
import co.edu.usbcali.ecommerceusb.repository.UserRepository;
import co.edu.usbcali.ecommerceusb.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<OrderResponse> getOrders() {
        List<Order> orders = orderRepository.findAll();

        if (orders.isEmpty()) {
            return List.of();
        }

        return OrderMapper.modelToOrderResponseList(orders);
    }

    @Override
    public OrderResponse getOrderById(Integer id) throws Exception {

        if (id == null || id <= 0) {
            throw new Exception("Debe ingresar el id para buscar");
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new Exception(
                                String.format("Orden no encontrada con el id: %d", id)));

        return OrderMapper.modelToOrderResponse(order);
    }

    @Override
    public OrderResponse createOrder(CreateOrderRequest createOrderRequest) throws Exception {

        // Validar campo userId
        if (Objects.isNull(createOrderRequest.getUserId()) ||
                createOrderRequest.getUserId() <= 0) {
            throw new Exception("El campo userId debe contener un valor mayor a 0");
        }

        // Validar campo status
        if (Objects.isNull(createOrderRequest.getStatus()) ||
                createOrderRequest.getStatus().isBlank()) {
            throw new Exception("El campo status no puede estar nulo ni vacío");
        }

        // Validar que el status sea un valor válido
        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(createOrderRequest.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new Exception("El status debe ser CREATED, PAID o CANCELLED");
        }

        // Validar campo totalAmount
        if (Objects.isNull(createOrderRequest.getTotalAmount()) ||
                createOrderRequest.getTotalAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("El campo totalAmount no puede ser nulo ni negativo");
        }

        // Validar campo currency
        if (Objects.isNull(createOrderRequest.getCurrency()) ||
                createOrderRequest.getCurrency().isBlank()) {
            throw new Exception("El campo currency no puede estar nulo ni vacío");
        }

        // Validar que el usuario existe
        User user = userRepository.findById(createOrderRequest.getUserId())
                .orElseThrow(() -> new Exception("El usuario no existe"));

        // Construir y guardar la orden
        Order order = Order.builder()
                .user(user)
                .status(orderStatus)
                .totalAmount(createOrderRequest.getTotalAmount())
                .currency(createOrderRequest.getCurrency().trim().toUpperCase())
                .createdAt(OffsetDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        return OrderMapper.modelToOrderResponse(savedOrder);
    }
}
