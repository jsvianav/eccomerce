package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateProductRequest;
import co.edu.usbcali.ecommerceusb.dto.ProductResponse;
import co.edu.usbcali.ecommerceusb.mapper.ProductMapper;
import co.edu.usbcali.ecommerceusb.model.Product;
import co.edu.usbcali.ecommerceusb.repository.ProductRepository;
import co.edu.usbcali.ecommerceusb.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<ProductResponse> getProducts() {
        List<Product> products = productRepository.findAll();

        if (products.isEmpty()) {
            return List.of();
        }

        return ProductMapper.modelToProductResponseList(products);
    }

    @Override
    public ProductResponse getProductById(Integer id) throws Exception {

        if (id == null || id <= 0) {
            throw new Exception("Debe ingresar el id para buscar");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new Exception(
                                String.format("Producto no encontrado con el id: %d", id)));

        return ProductMapper.modelToProductResponse(product);
    }

    @Override
    public ProductResponse createProduct(CreateProductRequest createProductRequest) throws Exception {

        // Validar campo name
        if (Objects.isNull(createProductRequest.getName()) ||
                createProductRequest.getName().isBlank()) {
            throw new Exception("El campo name no puede estar nulo ni vacío");
        }

        // Validar campo price
        if (Objects.isNull(createProductRequest.getPrice()) ||
                createProductRequest.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("El campo price no puede ser nulo ni negativo");
        }

        // Validar campo available
        if (Objects.isNull(createProductRequest.getAvailable())) {
            throw new Exception("El campo available no puede estar nulo");
        }

        // Construir y guardar el producto
        Product product = Product.builder()
                .name(createProductRequest.getName().trim())
                .description(createProductRequest.getDescription())
                .price(createProductRequest.getPrice())
                .available(createProductRequest.getAvailable())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        Product savedProduct = productRepository.save(product);

        return ProductMapper.modelToProductResponse(savedProduct);
    }
}
