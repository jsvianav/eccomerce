package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateOrderItemRequest;
import co.edu.usbcali.ecommerceusb.dto.OrderItemResponse;
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

    @Override
    public List<OrderItemResponse> getOrderItems() {
        List<OrderItem> orderItems = orderItemRepository.findAll();

        if (orderItems.isEmpty()) {
            return List.of();
        }

        return OrderItemMapper.modelToOrderItemResponseList(orderItems);
    }

    @Override
    public OrderItemResponse getOrderItemById(Integer id) throws Exception {

        if (id == null || id <= 0) {
            throw new Exception("Debe ingresar el id para buscar");
        }

        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() ->
                        new Exception(
                                String.format("Item de orden no encontrado con el id: %d", id)));

        return OrderItemMapper.modelToOrderItemResponse(orderItem);
    }

    @Override
    public OrderItemResponse createOrderItem(CreateOrderItemRequest createOrderItemRequest) throws Exception {

        // Validar campo orderId
        if (Objects.isNull(createOrderItemRequest.getOrderId()) ||
                createOrderItemRequest.getOrderId() <= 0) {
            throw new Exception("El campo orderId debe contener un valor mayor a 0");
        }

        // Validar campo productId
        if (Objects.isNull(createOrderItemRequest.getProductId()) ||
                createOrderItemRequest.getProductId() <= 0) {
            throw new Exception("El campo productId debe contener un valor mayor a 0");
        }

        // Validar campo quantity
        if (Objects.isNull(createOrderItemRequest.getQuantity()) ||
                createOrderItemRequest.getQuantity() <= 0) {
            throw new Exception("El campo quantity debe ser mayor a 0");
        }

        // Validar campo unitPriceSnapshot
        if (Objects.isNull(createOrderItemRequest.getUnitPriceSnapshot()) ||
                createOrderItemRequest.getUnitPriceSnapshot().compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("El campo unitPriceSnapshot no puede ser nulo ni negativo");
        }

        // Validar que la orden existe
        Order order = orderRepository.findById(createOrderItemRequest.getOrderId())
                .orElseThrow(() -> new Exception("La orden no existe"));

        // Validar que el producto existe
        Product product = productRepository.findById(createOrderItemRequest.getProductId())
                .orElseThrow(() -> new Exception("El producto no existe"));

        // Validar que no exista ya ese producto en la orden
        if (orderItemRepository.existsByOrderIdAndProductId(
                createOrderItemRequest.getOrderId(), createOrderItemRequest.getProductId())) {
            throw new Exception("Ya existe ese producto en la orden");
        }

        // Calcular lineTotal = quantity * unitPriceSnapshot
        BigDecimal lineTotal = createOrderItemRequest.getUnitPriceSnapshot()
                .multiply(BigDecimal.valueOf(createOrderItemRequest.getQuantity()));

        // Construir y guardar el item
        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(createOrderItemRequest.getQuantity())
                .unitPriceSnapshot(createOrderItemRequest.getUnitPriceSnapshot())
                .lineTotal(lineTotal)
                .createdAt(OffsetDateTime.now())
                .build();

        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        return OrderItemMapper.modelToOrderItemResponse(savedOrderItem);
    }
}
