package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateInventoryMovementRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteInventoryMovementResponse;
import co.edu.usbcali.ecommerceusb.dto.InventoryMovementResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateInventoryMovementRequest;
import co.edu.usbcali.ecommerceusb.exception.BadRequestException;
import co.edu.usbcali.ecommerceusb.exception.NotFoundException;
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

    /**
     * Retorna la lista de todos los movimientos de inventario registrados en la base de datos.
     * Si no existe ningún movimiento, retorna una lista vacía.
     */
    @Override
    public List<InventoryMovementResponse> getInventoryMovements() {
        List<InventoryMovement> movements = inventoryMovementRepository.findAll();
        if (movements.isEmpty()) return List.of();
        return InventoryMovementMapper.modelToInventoryMovementResponseList(movements);
    }

    /**
     * Busca y retorna un movimiento de inventario específico por su ID.
     * Lanza una excepción si el ID es inválido o si el movimiento no existe.
     */
    @Override
    public InventoryMovementResponse getInventoryMovementById(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar el id para buscar");
        InventoryMovement movement = inventoryMovementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Movimiento de inventario no encontrado con el id: %d", id)));
        return InventoryMovementMapper.modelToInventoryMovementResponse(movement);
    }

    /**
     * Crea un nuevo movimiento de inventario en la base de datos.
     * Valida que el request no sea nulo, que productId, type y qty sean válidos,
     * que el producto exista y, si se proporciona orderId, que la orden también exista.
     */
    @Override
    public InventoryMovementResponse createInventoryMovement(CreateInventoryMovementRequest req) {
        if (Objects.isNull(req))
            throw new BadRequestException("El objeto createInventoryMovementRequest no puede ser nulo");
        if (Objects.isNull(req.getProductId()) || req.getProductId() <= 0)
            throw new BadRequestException("El campo productId debe contener un valor mayor a 0");
        if (Objects.isNull(req.getType()) || req.getType().isBlank())
            throw new BadRequestException("El campo type no puede estar nulo ni vacío");
        // valueOf() lanza IllegalArgumentException si el valor no corresponde a ninguno de los valores del enum
        MovementType movementType;
        try {
            movementType = MovementType.valueOf(req.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("El type debe ser DEBIT, CREDIT, RESERVE o RELEASE");
        }
        if (Objects.isNull(req.getQty()) || req.getQty() <= 0)
            throw new BadRequestException("El campo qty debe ser mayor a 0");
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new NotFoundException("El producto no existe"));
        // El orderId es opcional: solo se busca la orden si fue proporcionado en el request
        Order order = null;
        if (req.getOrderId() != null) {
            order = orderRepository.findById(req.getOrderId())
                    .orElseThrow(() -> new NotFoundException("La orden no existe"));
        }
        InventoryMovement movement = InventoryMovement.builder()
                .product(product).order(order)
                .type(movementType)
                .qty(req.getQty())
                .createdAt(OffsetDateTime.now())
                .build();
        return InventoryMovementMapper.modelToInventoryMovementResponse(inventoryMovementRepository.save(movement));
    }

    /**
     * Actualiza la cantidad y/o el tipo de un movimiento de inventario existente.
     * Solo modifica los campos presentes en el request; lanza excepción si
     * el ID es inválido, el movimiento no existe o el tipo no es un valor permitido.
     */
    @Override
    public InventoryMovementResponse updateInventoryMovement(Integer id, UpdateInventoryMovementRequest req) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        InventoryMovement movement = inventoryMovementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Movimiento de inventario no encontrado con el id: %d", id)));
        if (req.getQty() != null && req.getQty() > 0) movement.setQty(req.getQty());
        if (req.getType() != null && !req.getType().isBlank()) {
            try {
                movement.setType(MovementType.valueOf(req.getType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("El type debe ser DEBIT, CREDIT, RESERVE o RELEASE");
            }
        }
        return InventoryMovementMapper.modelToInventoryMovementResponse(inventoryMovementRepository.save(movement));
    }

    /**
     * Elimina un InventoryMovement existente por su ID.
     * Lanza excepción si el ID es inválido o si el InventoryMovement no existe.
     */
    @Override
    public DeleteInventoryMovementResponse deleteInventoryMovement(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        InventoryMovement inventoryMovement = inventoryMovementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("InventoryMovement no encontrado con el id: %d", id)));
        inventoryMovementRepository.delete(inventoryMovement);
        return new DeleteInventoryMovementResponse("InventoryMovement con id " + id + " eliminado correctamente");
    }
}
