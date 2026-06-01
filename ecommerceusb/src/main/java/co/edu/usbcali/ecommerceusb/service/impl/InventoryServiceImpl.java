package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateInventoryRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteInventoryResponse;
import co.edu.usbcali.ecommerceusb.dto.InventoryResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateInventoryRequest;
import co.edu.usbcali.ecommerceusb.exception.BadRequestException;
import co.edu.usbcali.ecommerceusb.exception.InternalServerErrorException;
import co.edu.usbcali.ecommerceusb.exception.NotFoundException;
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
        if (inventories.isEmpty()) return List.of();
        return InventoryMapper.modelToInventoryResponseList(inventories);
    }

    /**
     * Busca y retorna un registro de inventario específico por su ID.
     * Lanza una excepción si el ID es inválido o si el inventario no existe.
     */
    @Override
    public InventoryResponse getInventoryById(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar el id para buscar");
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Inventario no encontrado con el id: %d", id)));
        return InventoryMapper.modelToInventoryResponse(inventory);
    }

    /**
     * Crea un nuevo registro de inventario para un producto.
     * Valida que el request no sea nulo, que productId y stock sean válidos,
     * que el producto exista, y que ese producto no tenga ya un inventario asignado.
     * Cada producto solo puede tener un único registro de inventario (relación 1 a 1).
     */
    @Override
    public InventoryResponse createInventory(CreateInventoryRequest createInventoryRequest) {
        if (Objects.isNull(createInventoryRequest))
            throw new BadRequestException("El objeto createInventoryRequest no puede ser nulo");
        if (Objects.isNull(createInventoryRequest.getProductId()) || createInventoryRequest.getProductId() <= 0)
            throw new BadRequestException("El campo productId debe contener un valor mayor a 0");
        if (Objects.isNull(createInventoryRequest.getStock()) || createInventoryRequest.getStock() < 0)
            throw new BadRequestException("El campo stock no puede ser nulo ni negativo");
        Product product = productRepository.findById(createInventoryRequest.getProductId())
                .orElseThrow(() -> new NotFoundException("El producto no existe"));
        // Inventario duplicado para el mismo producto → BadRequestException (409 semántico, 400 HTTP)
        if (inventoryRepository.existsByProductId(createInventoryRequest.getProductId()))
            throw new BadRequestException("Ya existe un inventario registrado para este producto");
        Inventory inventory = Inventory.builder()
                .product(product)
                .stock(createInventoryRequest.getStock())
                .updatedAt(OffsetDateTime.now())
                .build();
        return InventoryMapper.modelToInventoryResponse(inventoryRepository.save(inventory));
    }

    /**
     * Actualiza el stock de un inventario existente identificado por su ID.
     * Solo modifica el stock si viene en el request y no es negativo.
     * Lanza excepción si el ID es inválido o el inventario no existe.
     */
    @Override
    public InventoryResponse updateInventory(Integer id, UpdateInventoryRequest req) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Inventario no encontrado con el id: %d", id)));
        if (req.getQuantity() != null && req.getQuantity() >= 0) inventory.setStock(req.getQuantity());
        inventory.setUpdatedAt(OffsetDateTime.now());
        return InventoryMapper.modelToInventoryResponse(inventoryRepository.save(inventory));
    }

    /**
     * Elimina un Inventory existente por su ID.
     * Lanza excepción si el ID es inválido o si el Inventory no existe.
     */
    @Override
    public DeleteInventoryResponse deleteInventory(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Inventory no encontrado con el id: %d", id)));
        inventoryRepository.delete(inventory);
        return new DeleteInventoryResponse("Inventory con id " + id + " eliminado correctamente");
    }
}
