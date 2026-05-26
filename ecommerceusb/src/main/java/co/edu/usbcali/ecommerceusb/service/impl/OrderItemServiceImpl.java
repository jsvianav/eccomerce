package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateOrderItemRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteOrderItemResponse;
import co.edu.usbcali.ecommerceusb.dto.OrderItemResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateOrderItemRequest;
import co.edu.usbcali.ecommerceusb.exception.BadRequestException;
import co.edu.usbcali.ecommerceusb.exception.InternalServerErrorException;
import co.edu.usbcali.ecommerceusb.exception.NotFoundException;
import co.edu.usbcali.ecommerceusb.mapper.OrderItemMapper;
import co.edu.usbcali.ecommerceusb.model.Order;
import co.edu.usbcali.ecommerceusb.model.OrderItem;
import co.edu.usbcali.ecommerceusb.model.Product;
import co.edu.usbcali.ecommerceusb.repository.OrderItemRepository;
import co.edu.usbcali.ecommerceusb.repository.OrderRepository;
import co.edu.usbcali.ecommerceusb.repository.ProductRepository;
import co.edu.usbcali.ecommerceusb.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Retorna la lista de todos los items de orden registrados en la base de datos.
     * Si no existe ningún item, retorna una lista vacía.
     */
    @Override
    public List<OrderItemResponse> getOrderItems() {
        List<OrderItem> orderItems = orderItemRepository.findAll();
        if (orderItems.isEmpty()) return List.of();
        return OrderItemMapper.modelToOrderItemResponseList(orderItems);
    }

    /**
     * Busca y retorna un item de orden específico por su ID.
     * Lanza una excepción si el ID es inválido o si el item no existe.
     */
    @Override
    public OrderItemResponse getOrderItemById(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar el id para buscar");
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Item de orden no encontrado con el id: %d", id)));
        return OrderItemMapper.modelToOrderItemResponse(orderItem);
    }

    /**
     * Crea un nuevo item en una orden existente.
     * Valida que orderId, productId, quantity y unitPriceSnapshot sean válidos,
     * que la orden y el producto existan, que el producto no esté ya en esa orden,
     * y calcula automáticamente el lineTotal (precio unitario x cantidad).
     */
    @Override
    public OrderItemResponse createOrderItem(CreateOrderItemRequest createOrderItemRequest) {
        if (Objects.isNull(createOrderItemRequest))
            throw new BadRequestException("El objeto createOrderItemRequest no puede ser nulo");
        if (Objects.isNull(createOrderItemRequest.getOrderId()) || createOrderItemRequest.getOrderId() <= 0)
            throw new BadRequestException("El campo orderId debe contener un valor mayor a 0");
        if (Objects.isNull(createOrderItemRequest.getProductId()) || createOrderItemRequest.getProductId() <= 0)
            throw new BadRequestException("El campo productId debe contener un valor mayor a 0");
        if (Objects.isNull(createOrderItemRequest.getQuantity()) || createOrderItemRequest.getQuantity() <= 0)
            throw new BadRequestException("El campo quantity debe ser mayor a 0");
        if (Objects.isNull(createOrderItemRequest.getUnitPriceSnapshot()) ||
                createOrderItemRequest.getUnitPriceSnapshot().compareTo(BigDecimal.ZERO) < 0)
            throw new BadRequestException("El campo unitPriceSnapshot no puede ser nulo ni negativo");
        Order order = orderRepository.findById(createOrderItemRequest.getOrderId())
                .orElseThrow(() -> new NotFoundException("La orden no existe"));
        Product product = productRepository.findById(createOrderItemRequest.getProductId())
                .orElseThrow(() -> new NotFoundException("El producto no existe"));
        // Producto duplicado en la misma orden → InternalServerErrorException
        if (orderItemRepository.existsByOrderIdAndProductId(createOrderItemRequest.getOrderId(), createOrderItemRequest.getProductId()))
            throw new InternalServerErrorException("Ya existe ese producto en la orden");
        BigDecimal lineTotal = createOrderItemRequest.getUnitPriceSnapshot()
                .multiply(BigDecimal.valueOf(createOrderItemRequest.getQuantity()));
        OrderItem orderItem = OrderItem.builder()
                .order(order).product(product)
                .quantity(createOrderItemRequest.getQuantity())
                .unitPriceSnapshot(createOrderItemRequest.getUnitPriceSnapshot())
                .lineTotal(lineTotal)
                .createdAt(OffsetDateTime.now())
                .build();
        return OrderItemMapper.modelToOrderItemResponse(orderItemRepository.save(orderItem));
    }

    /**
     * Actualiza la cantidad y/o precio unitario de un item de orden existente.
     * Recalcula automáticamente el lineTotal si alguno de esos campos cambia.
     */
    @Override
    public OrderItemResponse updateOrderItem(Integer id, UpdateOrderItemRequest req) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Item de orden no encontrado con el id: %d", id)));
        if (req.getQuantity() != null && req.getQuantity() > 0) orderItem.setQuantity(req.getQuantity());
        if (req.getUnitPrice() != null && req.getUnitPrice().compareTo(BigDecimal.ZERO) >= 0)
            orderItem.setUnitPriceSnapshot(req.getUnitPrice());
        if (orderItem.getUnitPriceSnapshot() != null && orderItem.getQuantity() != null)
            orderItem.setLineTotal(orderItem.getUnitPriceSnapshot().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        return OrderItemMapper.modelToOrderItemResponse(orderItemRepository.save(orderItem));
    }

    /**
     * Elimina un OrderItem existente por su ID.
     * Lanza excepción si el ID es inválido o si el OrderItem no existe.
     */
    @Override
    public DeleteOrderItemResponse deleteOrderItem(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("OrderItem no encontrado con el id: %d", id)));
        orderItemRepository.delete(orderItem);
        return new DeleteOrderItemResponse("OrderItem con id " + id + " eliminado correctamente");
    }
}
