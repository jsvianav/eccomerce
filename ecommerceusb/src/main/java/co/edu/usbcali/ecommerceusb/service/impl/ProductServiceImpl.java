package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateProductRequest;
import co.edu.usbcali.ecommerceusb.dto.ProductResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateProductRequest;
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
        // Si no hay productos, retorna lista vacía en lugar de null
        if (products.isEmpty()) return List.of();
        // Convierte la lista de entidades al formato de respuesta
        return ProductMapper.modelToProductResponseList(products);
    }

    /**
     * Busca y retorna un producto específico por su ID.
     * Lanza una excepción si el ID es inválido o si el producto no existe.
     */
    @Override
    public ProductResponse getProductById(Integer id) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar el id para buscar");
        // Busca el producto; lanza excepción si no se encuentra
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Producto no encontrado con el id: %d", id)));
        // Convierte la entidad al objeto de respuesta y lo retorna
        return ProductMapper.modelToProductResponse(product);
    }

    /**
     * Crea un nuevo producto en la base de datos.
     * Valida que el request no sea nulo y que los campos obligatorios name y price
     * estén presentes. Usa el mapper para construir la entidad y la persiste.
     */
    @Override
    public ProductResponse createProduct(CreateProductRequest req) throws Exception {
        // Valida que el objeto request no sea nulo para evitar NullPointerException
        if (Objects.isNull(req))
            throw new Exception("El objeto createProductRequest no puede ser nulo");
        // Valida que el nombre del producto no esté vacío ni nulo
        if (Objects.isNull(req.getName()) || req.getName().isBlank())
            throw new Exception("El campo name no puede ser nulo ni vacío");
        // Valida que el precio no sea nulo (se permite precio 0 para productos gratuitos)
        if (req.getPrice() == null)
            throw new Exception("El campo price no puede ser nulo");
        // Usa el mapper para construir la entidad Product a partir del request
        Product product = ProductMapper.createProductRequestToProduct(req);
        // Guarda el producto en la base de datos
        productRepository.save(product);
        // Retorna la respuesta mapeada del producto creado
        return ProductMapper.modelToProductResponse(product);
    }

    /**
     * Actualiza los campos de un producto existente identificado por su ID.
     * Solo modifica los campos presentes en el request (name, description, price, available).
     * Actualiza automáticamente la marca de tiempo updatedAt al guardar.
     */
    @Override
    public ProductResponse updateProduct(Integer id, UpdateProductRequest req) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        // Busca el producto; lanza excepción si no se encuentra
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Producto no encontrado con el id: %d", id)));
        // Actualiza el nombre si viene en el request y no está vacío
        if (req.getName() != null && !req.getName().isBlank()) product.setName(req.getName());
        // Actualiza la descripción si viene en el request y no está vacía
        if (req.getDescription() != null && !req.getDescription().isBlank()) product.setDescription(req.getDescription());
        // Actualiza el precio si viene en el request
        if (req.getPrice() != null) product.setPrice(req.getPrice());
        // Actualiza la disponibilidad del producto si viene en el request
        if (req.getAvailable() != null) product.setAvailable(req.getAvailable());
        // Actualiza la marca de tiempo de la última modificación
        product.setUpdatedAt(OffsetDateTime.now());
        // Guarda los cambios en la base de datos
        productRepository.save(product);
        // Retorna la respuesta mapeada del producto actualizado
        return ProductMapper.modelToProductResponse(product);
    }
}
