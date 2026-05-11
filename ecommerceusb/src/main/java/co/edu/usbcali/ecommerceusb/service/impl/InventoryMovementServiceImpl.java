package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateInventoryMovementRequest;
import co.edu.usbcali.ecommerceusb.dto.InventoryMovementResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateInventoryMovementRequest;
import co.edu.usbcali.ecommerceusb.mapper.InventoryMovementMapper;
import co.edu.usbcali.ecommerceusb.model.InventoryMovement;
import co.edu.usbcali.ecommerceusb.model.InventoryMovement.MovementType;
import co.edu.usbcali.ecommerceusb.model.Order;
import co.edu.usbcali.ecommerceusb.model.Product;
import co.edu.usbcali.ecommerceusb.repository.InventoryMovementRepository;
import co.edu.usbcali.ecommerceusb.repository.OrderRepository;
import co.edu.usbcali.ecommerceusb.repository.ProductRepository;
import co.edu.usbcali.ecommerceusb.service.InventoryMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class InventoryMovementServiceImpl implements InventoryMovementService {

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<InventoryMovementResponse> getInventoryMovements() {
        List<InventoryMovement> movements = inventoryMovementRepository.findAll();
        if (movements.isEmpty()) return List.of();
        return InventoryMovementMapper.modelToInventoryMovementResponseList(movements);
    }

    @Override
    public InventoryMovementResponse getInventoryMovementById(Integer id) throws Exception {
        if (id == null || id <= 0) throw new Exception("Debe ingresar el id para buscar");
        InventoryMovement movement = inventoryMovementRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Movimiento de inventario no encontrado con el id: %d", id)));
        return InventoryMovementMapper.modelToInventoryMovementResponse(movement);
    }

    @Override
    public InventoryMovementResponse createInventoryMovement(CreateInventoryMovementRequest createInventoryMovementRequest) throws Exception {
        if (Objects.isNull(createInventoryMovementRequest.getProductId()) || createInventoryMovementRequest.getProductId() <= 0)
            throw new Exception("El campo productId debe contener un valor mayor a 0");
        if (Objects.isNull(createInventoryMovementRequest.getType()) || createInventoryMovementRequest.getType().isBlank())
            throw new Exception("El campo type no puede estar nulo ni vacío");
        MovementType movementType;
        try {
            movementType = MovementType.valueOf(createInventoryMovementRequest.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new Exception("El type debe ser DEBIT, CREDIT, RESERVE o RELEASE");
        }
        if (Objects.isNull(createInventoryMovementRequest.getQty()) || createInventoryMovementRequest.getQty() <= 0)
            throw new Exception("El campo qty debe ser mayor a 0");
        Product product = productRepository.findById(createInventoryMovementRequest.getProductId())
                .orElseThrow(() -> new Exception("El producto no existe"));
        Order order = null;
        if (createInventoryMovementRequest.getOrderId() != null) {
            order = orderRepository.findById(createInventoryMovementRequest.getOrderId())
                    .orElseThrow(() -> new Exception("La orden no existe"));
        }
        InventoryMovement movement = InventoryMovement.builder()
                .product(product).order(order)
                .type(movementType)
                .qty(createInventoryMovementRequest.getQty())
                .createdAt(OffsetDateTime.now())
                .build();
        return InventoryMovementMapper.modelToInventoryMovementResponse(inventoryMovementRepository.save(movement));
    }

    @Override
    public InventoryMovementResponse updateInventoryMovement(Integer id, UpdateInventoryMovementRequest req) throws Exception {
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        InventoryMovement movement = inventoryMovementRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Movimiento de inventario no encontrado con el id: %d", id)));
        if (req.getQuantity() != null && req.getQuantity() > 0) movement.setQty(req.getQuantity());
        if (req.getMovementType() != null && !req.getMovementType().isBlank()) {
            try {
                movement.setType(MovementType.valueOf(req.getMovementType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new Exception("El type debe ser DEBIT, CREDIT, RESERVE o RELEASE");
            }
        }
        if (req.getReason() != null && !req.getReason().isBlank()) movement.setReason(req.getReason());
        return InventoryMovementMapper.modelToInventoryMovementResponse(inventoryMovementRepository.save(movement));
    }
}
