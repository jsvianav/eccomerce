package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateOrderRequest;
import co.edu.usbcali.ecommerceusb.dto.OrderResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateOrderRequest;
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
        if (orders.isEmpty()) return List.of();
        return OrderMapper.modelToOrderResponseList(orders);
    }

    @Override
    public OrderResponse getOrderById(Integer id) throws Exception {
        if (id == null || id <= 0) throw new Exception("Debe ingresar el id para buscar");
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Orden no encontrada con el id: %d", id)));
        return OrderMapper.modelToOrderResponse(order);
    }

    @Override
    public OrderResponse createOrder(CreateOrderRequest req) throws Exception {
        if (Objects.isNull(req.getUserId()) || req.getUserId() <= 0)
            throw new Exception("El campo userId debe contener un valor mayor a 0");
        if (Objects.isNull(req.getStatus()) || req.getStatus().isBlank())
            throw new Exception("El campo status no puede estar nulo ni vacío");
        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(req.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new Exception("El status debe ser CREATED, PAID o CANCELLED");
        }
        if (Objects.isNull(req.getTotalAmount()) || req.getTotalAmount().compareTo(BigDecimal.ZERO) < 0)
            throw new Exception("El campo totalAmount no puede ser nulo ni negativo");
        if (Objects.isNull(req.getCurrency()) || req.getCurrency().isBlank())
            throw new Exception("El campo currency no puede estar nulo ni vacío");
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new Exception("El usuario no existe"));
        Order order = Order.builder()
                .user(user).status(orderStatus)
                .totalAmount(req.getTotalAmount())
                .currency(req.getCurrency().trim().toUpperCase())
                .createdAt(OffsetDateTime.now())
                .build();
        return OrderMapper.modelToOrderResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse updateOrder(Integer id, UpdateOrderRequest req) throws Exception {
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Orden no encontrada con el id: %d", id)));
        if (req.getStatus() != null && !req.getStatus().isBlank()) {
            try {
                order.setStatus(OrderStatus.valueOf(req.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new Exception("El status debe ser CREATED, PAID o CANCELLED");
            }
        }
        if (req.getTotalAmount() != null && req.getTotalAmount().compareTo(BigDecimal.ZERO) >= 0)
            order.setTotalAmount(req.getTotalAmount());
        if (req.getCurrency() != null && !req.getCurrency().isBlank())
            order.setCurrency(req.getCurrency().trim().toUpperCase());
        return OrderMapper.modelToOrderResponse(orderRepository.save(order));
    }
}
