package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateInventoryRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteInventoryResponse;
import co.edu.usbcali.ecommerceusb.dto.InventoryResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateInventoryRequest;
import co.edu.usbcali.ecommerceusb.mapper.InventoryMapper;
import co.edu.usbcali.ecommerceusb.model.Inventory;
import co.edu.usbcali.ecommerceusb.model.Product;
import co.edu.usbcali.ecommerceusb.repository.InventoryRepository;
import co.edu.usbcali.ecommerceusb.repository.ProductRepository;
import co.edu.usbcali.ecommerceusb.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Retorna la lista de todos los registros de inventario existentes en la base de datos.
     * Si no existe ningún registro, retorna una lista vacía.
     */
    @Override
    public List<InventoryResponse> getInventories() {
        List<Inventory> inventories = inventoryRepository.findAll();
        // Si no hay registros de inventario, retorna lista vacía en lugar de null
        if (inventories.isEmpty()) return List.of();
        // Convierte la lista de entidades al formato de respuesta
        return InventoryMapper.modelToInventoryResponseList(inventories);
    }

    /**
     * Busca y retorna un registro de inventario específico por su ID.
     * Lanza una excepción si el ID es inválido o si el inventario no existe.
     */
    @Override
    public InventoryResponse getInventoryById(Integer id) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar el id para buscar");
        // Busca el inventario; lanza excepción si no se encuentra
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Inventario no encontrado con el id: %d", id)));
        // Convierte la entidad al objeto de respuesta y lo retorna
        return InventoryMapper.modelToInventoryResponse(inventory);
    }

    /**
     * Crea un nuevo registro de inventario para un producto.
     * Valida que el request no sea nulo, que productId y stock sean válidos,
     * que el producto exista, y que ese producto no tenga ya un inventario asignado.
     * Cada producto solo puede tener un único registro de inventario (relación 1 a 1).
     */
    @Override
    public InventoryResponse createInventory(CreateInventoryRequest createInventoryRequest) throws Exception {
        // Valida que el objeto request no sea nulo para evitar NullPointerException
        if (Objects.isNull(createInventoryRequest))
            throw new Exception("El objeto createInventoryRequest no puede ser nulo");
        // Valida que el productId no sea nulo y sea mayor a 0
        if (Objects.isNull(createInventoryRequest.getProductId()) || createInventoryRequest.getProductId() <= 0)
            throw new Exception("El campo productId debe contener un valor mayor a 0");
        // Valida que el stock no sea nulo ni negativo (puede ser 0 si el producto está agotado)
        if (Objects.isNull(createInventoryRequest.getStock()) || createInventoryRequest.getStock() < 0)
            throw new Exception("El campo stock no puede ser nulo ni negativo");
        // Verifica que el producto exista en la base de datos
        Product product = productRepository.findById(createInventoryRequest.getProductId())
                .orElseThrow(() -> new Exception("El producto no existe"));
        // Verifica que ese producto no tenga ya un inventario registrado (relación única 1 a 1)
        if (inventoryRepository.existsByProductId(createInventoryRequest.getProductId()))
            throw new Exception("Ya existe un inventario para el producto ingresado");
        // Construye la entidad Inventory con los datos validados y la marca de tiempo actual
        Inventory inventory = Inventory.builder()
                .product(product)
                .stock(createInventoryRequest.getStock())
                .updatedAt(OffsetDateTime.now())
                .build();
        // Guarda el inventario en la base de datos y retorna la respuesta mapeada
        return InventoryMapper.modelToInventoryResponse(inventoryRepository.save(inventory));
    }

    /**
     * Actualiza el stock de un inventario existente identificado por su ID.
     * Solo modifica el stock si viene en el request y no es negativo.
     * Lanza excepción si el ID es inválido o el inventario no existe.
     */
    @Override
    public InventoryResponse updateInventory(Integer id, UpdateInventoryRequest req) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        // Busca el inventario; lanza excepción si no se encuentra
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Inventario no encontrado con el id: %d", id)));
        // Actualiza el stock si viene en el request y no es negativo (0 es válido: producto agotado)
        if (req.getQuantity() != null && req.getQuantity() >= 0) inventory.setStock(req.getQuantity());
        // Actualiza la marca de tiempo de la última modificación
        inventory.setUpdatedAt(OffsetDateTime.now());
        // Guarda los cambios en la base de datos y retorna la respuesta mapeada
        return InventoryMapper.modelToInventoryResponse(inventoryRepository.save(inventory));
    }
    /**
     * Elimina un Inventory existente por su ID.
     * Lanza excepción si el ID es inválido o si el Inventory no existe.
     */
    @Override
    public DeleteInventoryResponse deleteInventory(Integer id) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        // Busca el Inventory; lanza excepción si no se encuentra
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Inventory no encontrado con el id: %d", id)));
        // Elimina el registro de la base de datos
        inventoryRepository.delete(inventory);
        // Retorna la respuesta con mensaje de confirmación
        return new DeleteInventoryResponse("Inventory con id " + id + " eliminado correctamente");
    }
}
