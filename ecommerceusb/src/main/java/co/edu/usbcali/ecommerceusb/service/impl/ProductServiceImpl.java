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

    @Override
    public List<ProductResponse> getProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) return List.of();
        return ProductMapper.modelToProductResponseList(products);
    }

    @Override
    public ProductResponse getProductById(Integer id) throws Exception {
        if (id == null || id <= 0) throw new Exception("Debe ingresar el id para buscar");
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Producto no encontrado con el id: %d", id)));
        return ProductMapper.modelToProductResponse(product);
    }

    @Override
    public ProductResponse createProduct(CreateProductRequest req) throws Exception {
        if (Objects.isNull(req.getName()) || req.getName().isBlank())
            throw new Exception("El campo name no puede ser nulo ni vacío");
        if (req.getPrice() == null)
            throw new Exception("El campo price no puede ser nulo");
        Product product = ProductMapper.createProductRequestToProduct(req);
        productRepository.save(product);
        return ProductMapper.modelToProductResponse(product);
    }

    @Override
    public ProductResponse updateProduct(Integer id, UpdateProductRequest req) throws Exception {
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Producto no encontrado con el id: %d", id)));
        if (req.getName() != null && !req.getName().isBlank()) product.setName(req.getName());
        if (req.getDescription() != null && !req.getDescription().isBlank()) product.setDescription(req.getDescription());
        if (req.getPrice() != null) product.setPrice(req.getPrice());
        if (req.getAvailable() != null) product.setAvailable(req.getAvailable());
        product.setUpdatedAt(OffsetDateTime.now());
        productRepository.save(product);
        return ProductMapper.modelToProductResponse(product);
    }
}
