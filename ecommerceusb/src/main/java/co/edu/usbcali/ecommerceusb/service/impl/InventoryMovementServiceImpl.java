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
    public InventoryMovementResponse createInventoryMovement(CreateInventoryMovementRequest req) throws Exception {
        // Pedirle copia a Sebastian Baquero del comentario que le puse aquí

        if (Objects.isNull(req.getProductId()) || req.getProductId() <= 0)
            throw new Exception("El campo productId debe contener un valor mayor a 0");
        if (Objects.isNull(req.getType()) || req.getType().isBlank())
            throw new Exception("El campo type no puede estar nulo ni vacío");

        // Averiguar esta chimbada para que sirve
        MovementType movementType;
        try {
            movementType = MovementType.valueOf(req.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new Exception("El type debe ser DEBIT, CREDIT, RESERVE o RELEASE");
        }
        // Aqui termina lo que debemos saber para que sirve este codigo generado por IA :D

        if (Objects.isNull(req.getQty()) || req.getQty() <= 0)
            throw new Exception("El campo qty debe ser mayor a 0");
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new Exception("El producto no existe"));
        Order order = null;
        if (req.getOrderId() != null) {
            order = orderRepository.findById(req.getOrderId())
                    .orElseThrow(() -> new Exception("La orden no existe"));
        }
        InventoryMovement movement = InventoryMovement.builder()
                .product(product).order(order)
                .type(movementType)
                .qty(req.getQty())
                .createdAt(OffsetDateTime.now())
                .build();
        return InventoryMovementMapper.modelToInventoryMovementResponse(inventoryMovementRepository.save(movement));
    }

    @Override
    public InventoryMovementResponse updateInventoryMovement(Integer id, UpdateInventoryMovementRequest req) throws Exception {
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        InventoryMovement movement = inventoryMovementRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Movimiento de inventario no encontrado con el id: %d", id)));

        // Pedirle copia a Sebastian Baquero del otro comentario que le puse aquí

        if (req.getQuantity() != null && req.getQuantity() > 0) movement.setQty(req.getQuantity());
        if (req.getMovementType() != null && !req.getMovementType().isBlank()) {
            // Mismo comentario, esta vaina para que sirve
            try {
                movement.setType(MovementType.valueOf(req.getMovementType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new Exception("El type debe ser DEBIT, CREDIT, RESERVE o RELEASE");
            }
            // Mismo comentario, esta vaina para que sirve
        }
        return InventoryMovementMapper.modelToInventoryMovementResponse(inventoryMovementRepository.save(movement));
    }
}
