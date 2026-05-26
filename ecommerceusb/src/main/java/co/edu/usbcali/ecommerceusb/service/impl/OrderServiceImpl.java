package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateOrderRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteOrderResponse;
import co.edu.usbcali.ecommerceusb.dto.OrderResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateOrderRequest;
import co.edu.usbcali.ecommerceusb.exception.BadRequestException;
import co.edu.usbcali.ecommerceusb.exception.NotFoundException;
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

    /**
     * Retorna la lista de todas las órdenes registradas en la base de datos.
     * Si no existe ninguna orden, retorna una lista vacía.
     */
    @Override
    public List<OrderResponse> getOrders() {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) return List.of();
        return OrderMapper.modelToOrderResponseList(orders);
    }

    /**
     * Busca y retorna una orden específica por su ID.
     * Lanza una excepción si el ID es inválido o si la orden no existe.
     */
    @Override
    public OrderResponse getOrderById(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar el id para buscar");
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Orden no encontrada con el id: %d", id)));
        return OrderMapper.modelToOrderResponse(order);
    }

    /**
     * Crea una nueva orden en la base de datos.
     * Valida que el request no sea nulo, que userId, status, totalAmount y currency
     * sean válidos, y que el usuario exista antes de persistir la orden.
     */
    @Override
    public OrderResponse createOrder(CreateOrderRequest req) {
        if (Objects.isNull(req))
            throw new BadRequestException("El objeto createOrderRequest no puede ser nulo");
        if (Objects.isNull(req.getUserId()) || req.getUserId() <= 0)
            throw new BadRequestException("El campo userId debe contener un valor mayor a 0");
        if (Objects.isNull(req.getStatus()) || req.getStatus().isBlank())
            throw new BadRequestException("El campo status no puede estar nulo ni vacío");
        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(req.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("El status debe ser CREATED, PAID o CANCELLED");
        }
        if (Objects.isNull(req.getTotalAmount()) || req.getTotalAmount().compareTo(BigDecimal.ZERO) < 0)
            throw new BadRequestException("El campo totalAmount no puede ser nulo ni negativo");
        if (Objects.isNull(req.getCurrency()) || req.getCurrency().isBlank())
            throw new BadRequestException("El campo currency no puede estar nulo ni vacío");
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new NotFoundException("El usuario no existe"));
        Order order = Order.builder()
                .user(user).status(orderStatus)
                .totalAmount(req.getTotalAmount())
                .currency(req.getCurrency().trim().toUpperCase())
                .createdAt(OffsetDateTime.now())
                .build();
        return OrderMapper.modelToOrderResponse(orderRepository.save(order));
    }

    /**
     * Actualiza el estado, total y/o moneda de una orden existente.
     * Solo modifica los campos presentes en el request; lanza excepción si
     * el ID es inválido, la orden no existe o el status no es un valor permitido.
     */
    @Override
    public OrderResponse updateOrder(Integer id, UpdateOrderRequest req) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Orden no encontrada con el id: %d", id)));
        if (req.getStatus() != null && !req.getStatus().isBlank()) {
            try {
                order.setStatus(OrderStatus.valueOf(req.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("El status debe ser CREATED, PAID o CANCELLED");
            }
        }
        if (req.getTotalAmount() != null && req.getTotalAmount().compareTo(BigDecimal.ZERO) >= 0)
            order.setTotalAmount(req.getTotalAmount());
        if (req.getCurrency() != null && !req.getCurrency().isBlank())
            order.setCurrency(req.getCurrency().trim().toUpperCase());
        return OrderMapper.modelToOrderResponse(orderRepository.save(order));
    }

    /**
     * Elimina un Order existente por su ID.
     * Lanza excepción si el ID es inválido o si el Order no existe.
     */
    @Override
    public DeleteOrderResponse deleteOrder(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Order no encontrado con el id: %d", id)));
        orderRepository.delete(order);
        return new DeleteOrderResponse("Order con id " + id + " eliminado correctamente");
    }
}
