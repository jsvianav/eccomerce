package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateOrderItemRequest;
import co.edu.usbcali.ecommerceusb.dto.OrderItemResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateOrderItemRequest;
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
        if (orderItems.isEmpty()) return List.of();
        return OrderItemMapper.modelToOrderItemResponseList(orderItems);
    }

    @Override
    public OrderItemResponse getOrderItemById(Integer id) throws Exception {
        if (id == null || id <= 0) throw new Exception("Debe ingresar el id para buscar");
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Item de orden no encontrado con el id: %d", id)));
        return OrderItemMapper.modelToOrderItemResponse(orderItem);
    }

    @Override
    public OrderItemResponse createOrderItem(CreateOrderItemRequest createOrderItemRequest) throws Exception {
        if (Objects.isNull(createOrderItemRequest.getOrderId()) || createOrderItemRequest.getOrderId() <= 0)
            throw new Exception("El campo orderId debe contener un valor mayor a 0");
        if (Objects.isNull(createOrderItemRequest.getProductId()) || createOrderItemRequest.getProductId() <= 0)
            throw new Exception("El campo productId debe contener un valor mayor a 0");
        if (Objects.isNull(createOrderItemRequest.getQuantity()) || createOrderItemRequest.getQuantity() <= 0)
            throw new Exception("El campo quantity debe ser mayor a 0");
        if (Objects.isNull(createOrderItemRequest.getUnitPriceSnapshot()) ||
                createOrderItemRequest.getUnitPriceSnapshot().compareTo(BigDecimal.ZERO) < 0)
            throw new Exception("El campo unitPriceSnapshot no puede ser nulo ni negativo");
        Order order = orderRepository.findById(createOrderItemRequest.getOrderId())
                .orElseThrow(() -> new Exception("La orden no existe"));
        Product product = productRepository.findById(createOrderItemRequest.getProductId())
                .orElseThrow(() -> new Exception("El producto no existe"));
        if (orderItemRepository.existsByOrderIdAndProductId(createOrderItemRequest.getOrderId(), createOrderItemRequest.getProductId()))
            throw new Exception("Ya existe ese producto en la orden");
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

    @Override
    public OrderItemResponse updateOrderItem(Integer id, UpdateOrderItemRequest req) throws Exception {
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Item de orden no encontrado con el id: %d", id)));
        if (req.getQuantity() != null && req.getQuantity() > 0) orderItem.setQuantity(req.getQuantity());
        if (req.getUnitPrice() != null && req.getUnitPrice().compareTo(BigDecimal.ZERO) >= 0)
            orderItem.setUnitPriceSnapshot(req.getUnitPrice());
        if (orderItem.getUnitPriceSnapshot() != null && orderItem.getQuantity() != null)
            orderItem.setLineTotal(orderItem.getUnitPriceSnapshot().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        return OrderItemMapper.modelToOrderItemResponse(orderItemRepository.save(orderItem));
    }
}
