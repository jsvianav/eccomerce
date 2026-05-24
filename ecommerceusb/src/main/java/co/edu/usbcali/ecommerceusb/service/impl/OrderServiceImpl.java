package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateOrderRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteOrderResponse;
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

    /**
     * Retorna la lista de todas las órdenes registradas en la base de datos.
     * Si no existe ninguna orden, retorna una lista vacía.
     */
    @Override
    public List<OrderResponse> getOrders() {
        List<Order> orders = orderRepository.findAll();
        // Si no hay órdenes, retorna lista vacía en lugar de null
        if (orders.isEmpty()) return List.of();
        // Convierte la lista de entidades al formato de respuesta
        return OrderMapper.modelToOrderResponseList(orders);
    }

    /**
     * Busca y retorna una orden específica por su ID.
     * Lanza una excepción si el ID es inválido o si la orden no existe.
     */
    @Override
    public OrderResponse getOrderById(Integer id) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar el id para buscar");
        // Busca la orden; lanza excepción si no se encuentra
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Orden no encontrada con el id: %d", id)));
        // Convierte la entidad al objeto de respuesta y lo retorna
        return OrderMapper.modelToOrderResponse(order);
    }

    /**
     * Crea una nueva orden en la base de datos.
     * Valida que el request no sea nulo, que userId, status, totalAmount y currency
     * sean válidos, y que el usuario exista antes de persistir la orden.
     */
    @Override
    public OrderResponse createOrder(CreateOrderRequest req) throws Exception {
        // Valida que el objeto request no sea nulo
        if (Objects.isNull(req))
            throw new Exception("El objeto createOrderRequest no puede ser nulo");
        // Valida que el userId no sea nulo y sea mayor a 0
        if (Objects.isNull(req.getUserId()) || req.getUserId() <= 0)
            throw new Exception("El campo userId debe contener un valor mayor a 0");
        // Valida que el campo status no esté vacío ni nulo
        if (Objects.isNull(req.getStatus()) || req.getStatus().isBlank())
            throw new Exception("El campo status no puede estar nulo ni vacío");
        // Intenta convertir el string del status al enum OrderStatus
        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(req.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new Exception("El status debe ser CREATED, PAID o CANCELLED");
        }
        // Valida que el total no sea nulo ni negativo
        if (Objects.isNull(req.getTotalAmount()) || req.getTotalAmount().compareTo(BigDecimal.ZERO) < 0)
            throw new Exception("El campo totalAmount no puede ser nulo ni negativo");
        // Valida que la moneda no esté vacía ni nula
        if (Objects.isNull(req.getCurrency()) || req.getCurrency().isBlank())
            throw new Exception("El campo currency no puede estar nulo ni vacío");
        // Verifica que el usuario exista en la base de datos
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new Exception("El usuario no existe"));
        // Construye la entidad Order con los datos validados
        Order order = Order.builder()
                .user(user).status(orderStatus)
                .totalAmount(req.getTotalAmount())
                .currency(req.getCurrency().trim().toUpperCase())
                .createdAt(OffsetDateTime.now())
                .build();
        // Guarda la orden en la base de datos y retorna la respuesta mapeada
        return OrderMapper.modelToOrderResponse(orderRepository.save(order));
    }

    /**
     * Actualiza el estado, total y/o moneda de una orden existente.
     * Solo modifica los campos presentes en el request; lanza excepción si
     * el ID es inválido, la orden no existe o el status no es un valor permitido.
     */
    @Override
    public OrderResponse updateOrder(Integer id, UpdateOrderRequest req) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        // Busca la orden; lanza excepción si no se encuentra
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Orden no encontrada con el id: %d", id)));
        // Actualiza el status si viene en el request, validando que sea un valor permitido
        if (req.getStatus() != null && !req.getStatus().isBlank()) {
            try {
                order.setStatus(OrderStatus.valueOf(req.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new Exception("El status debe ser CREATED, PAID o CANCELLED");
            }
        }
        // Actualiza el total si viene en el request y no es negativo
        if (req.getTotalAmount() != null && req.getTotalAmount().compareTo(BigDecimal.ZERO) >= 0)
            order.setTotalAmount(req.getTotalAmount());
        // Actualiza la moneda si viene en el request y no está vacía
        if (req.getCurrency() != null && !req.getCurrency().isBlank())
            order.setCurrency(req.getCurrency().trim().toUpperCase());
        // Guarda los cambios y retorna la respuesta mapeada
        return OrderMapper.modelToOrderResponse(orderRepository.save(order));
    }
    /**
     * Elimina un Order existente por su ID.
     * Lanza excepción si el ID es inválido o si el Order no existe.
     */
    @Override
    public DeleteOrderResponse deleteOrder(Integer id) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        // Busca el Order; lanza excepción si no se encuentra
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Order no encontrado con el id: %d", id)));
        // Elimina el registro de la base de datos
        orderRepository.delete(order);
        // Retorna la respuesta con mensaje de confirmación
        return new DeleteOrderResponse("Order con id " + id + " eliminado correctamente");
    }
}
