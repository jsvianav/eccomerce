package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateInventoryMovementRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteInventoryMovementResponse;
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

    /**
     * Retorna la lista de todos los movimientos de inventario registrados en la base de datos.
     * Si no existe ningún movimiento, retorna una lista vacía.
     */
    @Override
    public List<InventoryMovementResponse> getInventoryMovements() {
        List<InventoryMovement> movements = inventoryMovementRepository.findAll();
        // Si no hay movimientos, retorna lista vacía en lugar de null
        if (movements.isEmpty()) return List.of();
        // Convierte la lista de entidades al formato de respuesta
        return InventoryMovementMapper.modelToInventoryMovementResponseList(movements);
    }

    /**
     * Busca y retorna un movimiento de inventario específico por su ID.
     * Lanza una excepción si el ID es inválido o si el movimiento no existe.
     */
    @Override
    public InventoryMovementResponse getInventoryMovementById(Integer id) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar el id para buscar");
        // Busca el movimiento; lanza excepción si no se encuentra
        InventoryMovement movement = inventoryMovementRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Movimiento de inventario no encontrado con el id: %d", id)));
        // Convierte la entidad al objeto de respuesta y lo retorna
        return InventoryMovementMapper.modelToInventoryMovementResponse(movement);
    }

    /**
     * Crea un nuevo movimiento de inventario en la base de datos.
     * Valida que el request no sea nulo, que productId, type y qty sean válidos,
     * que el producto exista y, si se proporciona orderId, que la orden también exista.
     */
    @Override
    public InventoryMovementResponse createInventoryMovement(CreateInventoryMovementRequest req) throws Exception {
        // Valida que el objeto request no sea nulo para evitar NullPointerException
        if (Objects.isNull(req))
            throw new Exception("El objeto createInventoryMovementRequest no puede ser nulo");
        // Valida que el productId no sea nulo y sea mayor a 0
        if (Objects.isNull(req.getProductId()) || req.getProductId() <= 0)
            throw new Exception("El campo productId debe contener un valor mayor a 0");
        // Valida que el campo type no esté vacío ni nulo
        if (Objects.isNull(req.getType()) || req.getType().isBlank())
            throw new Exception("El campo type no puede estar nulo ni vacío");
        // Intenta convertir el String recibido al enum MovementType (DEBIT, CREDIT, RESERVE, RELEASE).
        // valueOf() lanza IllegalArgumentException si el valor no corresponde a ninguno de los valores del enum,
        // por eso se envuelve en un try-catch para retornar un mensaje de error claro al usuario.
        MovementType movementType;
        try {
            movementType = MovementType.valueOf(req.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new Exception("El type debe ser DEBIT, CREDIT, RESERVE o RELEASE");
        }
        // Valida que la cantidad (qty) no sea nula y sea mayor a 0
        if (Objects.isNull(req.getQty()) || req.getQty() <= 0)
            throw new Exception("El campo qty debe ser mayor a 0");
        // Verifica que el producto exista en la base de datos
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new Exception("El producto no existe"));
        // El orderId es opcional: solo se busca la orden si fue proporcionado en el request
        Order order = null;
        if (req.getOrderId() != null) {
            order = orderRepository.findById(req.getOrderId())
                    .orElseThrow(() -> new Exception("La orden no existe"));
        }
        // Construye la entidad InventoryMovement con los datos validados y la marca de tiempo actual
        InventoryMovement movement = InventoryMovement.builder()
                .product(product).order(order)
                .type(movementType)
                .qty(req.getQty())
                .createdAt(OffsetDateTime.now())
                .build();
        // Guarda el movimiento en la base de datos y retorna la respuesta mapeada
        return InventoryMovementMapper.modelToInventoryMovementResponse(inventoryMovementRepository.save(movement));
    }

    /**
     * Actualiza la cantidad y/o el tipo de un movimiento de inventario existente.
     * Solo modifica los campos presentes en el request; lanza excepción si
     * el ID es inválido, el movimiento no existe o el tipo no es un valor permitido.
     */
    @Override
    public InventoryMovementResponse updateInventoryMovement(Integer id, UpdateInventoryMovementRequest req) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        // Busca el movimiento; lanza excepción si no se encuentra
        InventoryMovement movement = inventoryMovementRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Movimiento de inventario no encontrado con el id: %d", id)));
        // Actualiza la cantidad si viene en el request y es mayor a 0
        if (req.getQuantity() != null && req.getQuantity() > 0) movement.setQty(req.getQuantity());
        // Actualiza el tipo de movimiento si viene en el request y no está vacío
        if (req.getMovementType() != null && !req.getMovementType().isBlank()) {
            // Intenta convertir el String al enum MovementType; si no es válido lanza excepción con mensaje claro
            try {
                movement.setType(MovementType.valueOf(req.getMovementType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new Exception("El type debe ser DEBIT, CREDIT, RESERVE o RELEASE");
            }
        }
        // Guarda los cambios en la base de datos y retorna la respuesta mapeada
        return InventoryMovementMapper.modelToInventoryMovementResponse(inventoryMovementRepository.save(movement));
    }
    /**
     * Elimina un InventoryMovement existente por su ID.
     * Lanza excepción si el ID es inválido o si el InventoryMovement no existe.
     */
    @Override
    public DeleteInventoryMovementResponse deleteInventoryMovement(Integer id) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        // Busca el InventoryMovement; lanza excepción si no se encuentra
        InventoryMovement inventoryMovement = inventoryMovementRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("InventoryMovement no encontrado con el id: %d", id)));
        // Elimina el registro de la base de datos
        inventoryMovementRepository.delete(inventoryMovement);
        // Retorna la respuesta con mensaje de confirmación
        return new DeleteInventoryMovementResponse("InventoryMovement con id " + id + " eliminado correctamente");
    }
}
