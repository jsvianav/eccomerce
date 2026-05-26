package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateProductCategoryRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteProductCategoryResponse;
import co.edu.usbcali.ecommerceusb.dto.ProductCategoryResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateProductCategoryRequest;
import co.edu.usbcali.ecommerceusb.exception.BadRequestException;
import co.edu.usbcali.ecommerceusb.exception.InternalServerErrorException;
import co.edu.usbcali.ecommerceusb.exception.NotFoundException;
import co.edu.usbcali.ecommerceusb.mapper.ProductCategoryMapper;
import co.edu.usbcali.ecommerceusb.model.Category;
import co.edu.usbcali.ecommerceusb.model.Product;
import co.edu.usbcali.ecommerceusb.model.ProductCategory;
import co.edu.usbcali.ecommerceusb.repository.CategoryRepository;
import co.edu.usbcali.ecommerceusb.repository.ProductCategoryRepository;
import co.edu.usbcali.ecommerceusb.repository.ProductRepository;
import co.edu.usbcali.ecommerceusb.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Retorna la lista de todas las asignaciones producto-categoría registradas.
     * Si no existe ninguna, retorna una lista vacía.
     */
    @Override
    public List<ProductCategoryResponse> getProductCategories() {
        List<ProductCategory> productCategories = productCategoryRepository.findAll();
        if (productCategories.isEmpty()) return List.of();
        return ProductCategoryMapper.modelToProductCategoryResponseList(productCategories);
    }

    /**
     * Busca y retorna una asignación producto-categoría específica por su ID.
     * Lanza una excepción si el ID es inválido o si la asignación no existe.
     */
    @Override
    public ProductCategoryResponse getProductCategoryById(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar el id para buscar");
        ProductCategory productCategory = productCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Asignación producto-categoría no encontrada con el id: %d", id)));
        return ProductCategoryMapper.modelToProductCategoryResponse(productCategory);
    }

    /**
     * Crea una nueva asignación entre un producto y una categoría.
     * Valida que productId y categoryId sean válidos, que ambos existan en la BD,
     * y que la combinación producto-categoría no esté ya registrada.
     */
    @Override
    public ProductCategoryResponse createProductCategory(CreateProductCategoryRequest req) {
        if (Objects.isNull(req))
            throw new BadRequestException("El objeto createProductCategoryRequest no puede ser nulo");
        if (Objects.isNull(req.getProductId()) || req.getProductId() <= 0)
            throw new BadRequestException("El campo productId debe contener un valor mayor a 0");
        if (Objects.isNull(req.getCategoryId()) || req.getCategoryId() <= 0)
            throw new BadRequestException("El campo categoryId debe contener un valor mayor a 0");
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new NotFoundException("El producto no existe"));
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new NotFoundException("La categoría no existe"));
        // Combinación producto-categoría duplicada → InternalServerErrorException
        if (productCategoryRepository.existsByProductIdAndCategoryId(req.getProductId(), req.getCategoryId()))
            throw new InternalServerErrorException("El producto ya está asignado a esa categoría");
        ProductCategory productCategory = ProductCategory.builder()
                .product(product)
                .category(category)
                .build();
        return ProductCategoryMapper.modelToProductCategoryResponse(productCategoryRepository.save(productCategory));
    }

    /**
     * Actualiza la asignación producto-categoría existente.
     * Permite cambiar el producto, la categoría, o ambos.
     * Valida que la nueva combinación no genere un duplicado.
     */
    @Override
    public ProductCategoryResponse updateProductCategory(Integer id, UpdateProductCategoryRequest req) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        ProductCategory productCategory = productCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Asignación producto-categoría no encontrada con el id: %d", id)));
        if (req.getProductId() != null && req.getProductId() > 0) {
            Product product = productRepository.findById(req.getProductId())
                    .orElseThrow(() -> new NotFoundException("El producto no existe"));
            productCategory.setProduct(product);
        }
        if (req.getCategoryId() != null && req.getCategoryId() > 0) {
            Category category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("La categoría no existe"));
            productCategory.setCategory(category);
        }
        // Verifica que la nueva combinación no duplique otra asignación existente
        if (productCategoryRepository.existsByProductIdAndCategoryId(
                productCategory.getProduct().getId(), productCategory.getCategory().getId())) {
            throw new InternalServerErrorException("El producto ya está asignado a esa categoría");
        }
        return ProductCategoryMapper.modelToProductCategoryResponse(productCategoryRepository.save(productCategory));
    }

    /**
     * Elimina una asignación producto-categoría por su ID.
     * Lanza excepción si el ID es inválido o si la asignación no existe.
     */
    @Override
    public DeleteProductCategoryResponse deleteProductCategory(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        ProductCategory productCategory = productCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Asignación producto-categoría no encontrada con el id: %d", id)));
        productCategoryRepository.delete(productCategory);
        return new DeleteProductCategoryResponse("Asignación producto-categoría con id " + id + " eliminada correctamente");
    }
}
