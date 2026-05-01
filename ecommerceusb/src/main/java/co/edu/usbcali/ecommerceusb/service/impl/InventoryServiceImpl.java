package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateInventoryRequest;
import co.edu.usbcali.ecommerceusb.dto.InventoryResponse;
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

    @Override
    public List<InventoryResponse> getInventories() {
        List<Inventory> inventories = inventoryRepository.findAll();

        if (inventories.isEmpty()) {
            return List.of();
        }

        return InventoryMapper.modelToInventoryResponseList(inventories);
    }

    @Override
    public InventoryResponse getInventoryById(Integer id) throws Exception {

        if (id == null || id <= 0) {
            throw new Exception("Debe ingresar el id para buscar");
        }

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() ->
                        new Exception(
                                String.format("Inventario no encontrado con el id: %d", id)));

        return InventoryMapper.modelToInventoryResponse(inventory);
    }

    @Override
    public InventoryResponse createInventory(CreateInventoryRequest createInventoryRequest) throws Exception {

        // Validar campo productId
        if (Objects.isNull(createInventoryRequest.getProductId()) ||
                createInventoryRequest.getProductId() <= 0) {
            throw new Exception("El campo productId debe contener un valor mayor a 0");
        }

        // Validar campo stock
        if (Objects.isNull(createInventoryRequest.getStock()) ||
                createInventoryRequest.getStock() < 0) {
            throw new Exception("El campo stock no puede ser nulo ni negativo");
        }

        // Validar que el producto existe
        Product product = productRepository.findById(createInventoryRequest.getProductId())
                .orElseThrow(() -> new Exception("El producto no existe"));

        // Validar que el producto no tenga ya un inventario (relación OneToOne)
        if (inventoryRepository.existsByProductId(createInventoryRequest.getProductId())) {
            throw new Exception("Ya existe un inventario para el producto ingresado");
        }

        // Construir y guardar el inventario
        Inventory inventory = Inventory.builder()
                .product(product)
                .stock(createInventoryRequest.getStock())
                .updatedAt(OffsetDateTime.now())
                .build();

        Inventory savedInventory = inventoryRepository.save(inventory);

        return InventoryMapper.modelToInventoryResponse(savedInventory);
    }
}
