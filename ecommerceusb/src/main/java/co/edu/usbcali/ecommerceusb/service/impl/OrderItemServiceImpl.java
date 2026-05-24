package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateOrderItemRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteOrderItemResponse;
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

    /**
     * Retorna la lista de todos los items de orden registrados en la base de datos.
     * Si no existe ningún item, retorna una lista vacía.
     */
    @Override
    public List<OrderItemResponse> getOrderItems() {
        List<OrderItem> orderItems = orderItemRepository.findAll();
        // Si no hay items de orden, retorna lista vacía en lugar de null
        if (orderItems.isEmpty()) return List.of();
        // Convierte la lista de entidades al formato de respuesta
        return OrderItemMapper.modelToOrderItemResponseList(orderItems);
    }

    /**
     * Busca y retorna un item de orden específico por su ID.
     * Lanza una excepción si el ID es inválido o si el item no existe.
     */
    @Override
    public OrderItemResponse getOrderItemById(Integer id) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar el id para buscar");
        // Busca el item de orden; lanza excepción si no se encuentra
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Item de orden no encontrado con el id: %d", id)));
        // Convierte la entidad al objeto de respuesta y lo retorna
        return OrderItemMapper.modelToOrderItemResponse(orderItem);
    }

    /**
     * Crea un nuevo item en una orden existente.
     * Valida que orderId, productId, quantity y unitPriceSnapshot sean válidos,
     * que la orden y el producto existan, que el producto no esté ya en esa orden,
     * y calcula automáticamente el lineTotal (precio unitario x cantidad).
     */
    @Override
    public OrderItemResponse createOrderItem(CreateOrderItemRequest createOrderItemRequest) throws Exception {
        // Valida que el objeto request no sea nulo
        if (Objects.isNull(createOrderItemRequest))
            throw new Exception("El objeto createOrderItemRequest no puede ser nulo");
        // Valida que el orderId no sea nulo y sea mayor a 0
        if (Objects.isNull(createOrderItemRequest.getOrderId()) || createOrderItemRequest.getOrderId() <= 0)
            throw new Exception("El campo orderId debe contener un valor mayor a 0");
        // Valida que el productId no sea nulo y sea mayor a 0
        if (Objects.isNull(createOrderItemRequest.getProductId()) || createOrderItemRequest.getProductId() <= 0)
            throw new Exception("El campo productId debe contener un valor mayor a 0");
        // Valida que la cantidad sea mayor a 0
        if (Objects.isNull(createOrderItemRequest.getQuantity()) || createOrderItemRequest.getQuantity() <= 0)
            throw new Exception("El campo quantity debe ser mayor a 0");
        // Valida que el precio unitario no sea nulo ni negativo
        if (Objects.isNull(createOrderItemRequest.getUnitPriceSnapshot()) ||
                createOrderItemRequest.getUnitPriceSnapshot().compareTo(BigDecimal.ZERO) < 0)
            throw new Exception("El campo unitPriceSnapshot no puede ser nulo ni negativo");
        // Verifica que la orden exista en la base de datos
        Order order = orderRepository.findById(createOrderItemRequest.getOrderId())
                .orElseThrow(() -> new Exception("La orden no existe"));
        // Verifica que el producto exista en la base de datos
        Product product = productRepository.findById(createOrderItemRequest.getProductId())
                .orElseThrow(() -> new Exception("El producto no existe"));
        // Verifica que el producto no esté ya agregado en esa orden (evita duplicados)
        if (orderItemRepository.existsByOrderIdAndProductId(createOrderItemRequest.getOrderId(), createOrderItemRequest.getProductId()))
            throw new Exception("Ya existe ese producto en la orden");
        // Calcula el total de la línea: precio unitario x cantidad
        BigDecimal lineTotal = createOrderItemRequest.getUnitPriceSnapshot()
                .multiply(BigDecimal.valueOf(createOrderItemRequest.getQuantity()));
        // Construye el item con los datos validados
        OrderItem orderItem = OrderItem.builder()
                .order(order).product(product)
                .quantity(createOrderItemRequest.getQuantity())
                .unitPriceSnapshot(createOrderItemRequest.getUnitPriceSnapshot())
                .lineTotal(lineTotal)
                .createdAt(OffsetDateTime.now())
                .build();
        // Guarda el item en la base de datos y retorna la respuesta mapeada
        return OrderItemMapper.modelToOrderItemResponse(orderItemRepository.save(orderItem));
    }

    /**
     * Actualiza la cantidad y/o precio unitario de un item de orden existente.
     * Recalcula automáticamente el lineTotal si alguno de esos campos cambia.
     */
    @Override
    public OrderItemResponse updateOrderItem(Integer id, UpdateOrderItemRequest req) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        // Busca el item de orden; lanza excepción si no se encuentra
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Item de orden no encontrado con el id: %d", id)));
        // Actualiza la cantidad si viene en el request y es mayor a 0
        if (req.getQuantity() != null && req.getQuantity() > 0) orderItem.setQuantity(req.getQuantity());
        // Actualiza el precio unitario si viene en el request y no es negativo
        if (req.getUnitPrice() != null && req.getUnitPrice().compareTo(BigDecimal.ZERO) >= 0)
            orderItem.setUnitPriceSnapshot(req.getUnitPrice());
        // Recalcula el lineTotal con los valores actualizados
        if (orderItem.getUnitPriceSnapshot() != null && orderItem.getQuantity() != null)
            orderItem.setLineTotal(orderItem.getUnitPriceSnapshot().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        // Guarda los cambios y retorna la respuesta mapeada
        return OrderItemMapper.modelToOrderItemResponse(orderItemRepository.save(orderItem));
    }
    /**
     * Elimina un OrderItem existente por su ID.
     * Lanza excepción si el ID es inválido o si el OrderItem no existe.
     */
    @Override
    public DeleteOrderItemResponse deleteOrderItem(Integer id) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        // Busca el OrderItem; lanza excepción si no se encuentra
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("OrderItem no encontrado con el id: %d", id)));
        // Elimina el registro de la base de datos
        orderItemRepository.delete(orderItem);
        // Retorna la respuesta con mensaje de confirmación
        return new DeleteOrderItemResponse("OrderItem con id " + id + " eliminado correctamente");
    }
}
