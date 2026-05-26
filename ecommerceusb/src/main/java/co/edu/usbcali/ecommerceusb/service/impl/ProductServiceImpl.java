package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateProductRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteProductResponse;
import co.edu.usbcali.ecommerceusb.dto.ProductResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateProductRequest;
import co.edu.usbcali.ecommerceusb.exception.BadRequestException;
import co.edu.usbcali.ecommerceusb.exception.NotFoundException;
import co.edu.usbcali.ecommerceusb.mapper.ProductMapper;
import co.edu.usbcali.ecommerceusb.model.Product;
import co.edu.usbcali.ecommerceusb.repository.ProductRepository;
import co.edu.usbcali.ecommerceusb.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Retorna la lista de todos los productos registrados en la base de datos.
     * Si no existe ningún producto, retorna una lista vacía.
     */
    @Override
    public List<ProductResponse> getProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) return List.of();
        return ProductMapper.modelToProductResponseList(products);
    }

    /**
     * Busca y retorna un producto específico por su ID.
     * Lanza una excepción si el ID es inválido o si el producto no existe.
     */
    @Override
    public ProductResponse getProductById(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar el id para buscar");
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Producto no encontrado con el id: %d", id)));
        return ProductMapper.modelToProductResponse(product);
    }

    /**
     * Crea un nuevo producto en la base de datos.
     * Valida que el request no sea nulo y que los campos obligatorios name y price
     * estén presentes. Usa el mapper para construir la entidad y la persiste.
     */
    @Override
    public ProductResponse createProduct(CreateProductRequest req) {
        if (Objects.isNull(req))
            throw new BadRequestException("El objeto createProductRequest no puede ser nulo");
        if (Objects.isNull(req.getName()) || req.getName().isBlank())
            throw new BadRequestException("El campo name no puede ser nulo ni vacío");
        if (req.getPrice() == null)
            throw new BadRequestException("El campo price no puede ser nulo");
        Product product = ProductMapper.createProductRequestToProduct(req);
        productRepository.save(product);
        return ProductMapper.modelToProductResponse(product);
    }

    /**
     * Actualiza los campos de un producto existente identificado por su ID.
     * Solo modifica los campos presentes en el request (name, description, price, available).
     * Actualiza automáticamente la marca de tiempo updatedAt al guardar.
     */
    @Override
    public ProductResponse updateProduct(Integer id, UpdateProductRequest req) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Producto no encontrado con el id: %d", id)));
        if (req.getName() != null && !req.getName().isBlank()) product.setName(req.getName());
        if (req.getDescription() != null && !req.getDescription().isBlank()) product.setDescription(req.getDescription());
        if (req.getPrice() != null) product.setPrice(req.getPrice());
        if (req.getAvailable() != null) product.setAvailable(req.getAvailable());
        product.setUpdatedAt(OffsetDateTime.now());
        productRepository.save(product);
        return ProductMapper.modelToProductResponse(product);
    }

    /**
     * Elimina un Product existente por su ID.
     * Lanza excepción si el ID es inválido o si el Product no existe.
     */
    @Override
    public DeleteProductResponse deleteProduct(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Product no encontrado con el id: %d", id)));
        productRepository.delete(product);
        return new DeleteProductResponse("Product con id " + id + " eliminado correctamente");
    }
}
