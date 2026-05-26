package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CartItemResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCartItemRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteCartItemResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateCartItemRequest;
import co.edu.usbcali.ecommerceusb.exception.BadRequestException;
import co.edu.usbcali.ecommerceusb.exception.InternalServerErrorException;
import co.edu.usbcali.ecommerceusb.exception.NotFoundException;
import co.edu.usbcali.ecommerceusb.mapper.CartItemMapper;
import co.edu.usbcali.ecommerceusb.model.Cart;
import co.edu.usbcali.ecommerceusb.model.CartItem;
import co.edu.usbcali.ecommerceusb.model.Product;
import co.edu.usbcali.ecommerceusb.repository.CartItemRepository;
import co.edu.usbcali.ecommerceusb.repository.CartRepository;
import co.edu.usbcali.ecommerceusb.repository.ProductRepository;
import co.edu.usbcali.ecommerceusb.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class CartItemServiceImpl implements CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Retorna la lista de todos los items de carrito registrados en la base de datos.
     * Si no existe ningún item, retorna una lista vacía.
     */
    @Override
    public List<CartItemResponse> getCartItems() {
        List<CartItem> cartItems = cartItemRepository.findAll();
        if (cartItems.isEmpty()) return List.of();
        return CartItemMapper.modelToCartItemResponseList(cartItems);
    }

    /**
     * Busca y retorna un item de carrito específico por su ID.
     * Lanza una excepción si el ID es inválido o si el item no existe.
     */
    @Override
    public CartItemResponse getCartItemById(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar el id para buscar");
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Item de carrito no encontrado con el id: %d", id)));
        return CartItemMapper.modelToCartItemResponse(cartItem);
    }

    /**
     * Crea un nuevo item en un carrito existente.
     * Valida que el request no sea nulo, que cartId, productId y quantity sean válidos,
     * que el carrito y el producto existan, y que el producto no esté ya en ese carrito.
     */
    @Override
    public CartItemResponse createCartItem(CreateCartItemRequest createCartItemRequest) {
        if (Objects.isNull(createCartItemRequest))
            throw new BadRequestException("El objeto createCartItemRequest no puede ser nulo");
        if (Objects.isNull(createCartItemRequest.getCartId()) || createCartItemRequest.getCartId() <= 0)
            throw new BadRequestException("El campo cartId debe contener un valor mayor a 0");
        if (Objects.isNull(createCartItemRequest.getProductId()) || createCartItemRequest.getProductId() <= 0)
            throw new BadRequestException("El campo productId debe contener un valor mayor a 0");
        if (Objects.isNull(createCartItemRequest.getQuantity()) || createCartItemRequest.getQuantity() <= 0)
            throw new BadRequestException("El campo quantity debe ser mayor a 0");
        Cart cart = cartRepository.findById(createCartItemRequest.getCartId())
                .orElseThrow(() -> new NotFoundException("El carrito no existe"));
        Product product = productRepository.findById(createCartItemRequest.getProductId())
                .orElseThrow(() -> new NotFoundException("El producto no existe"));
        // Producto duplicado en el mismo carrito → InternalServerErrorException
        if (cartItemRepository.existsByCartIdAndProductId(createCartItemRequest.getCartId(), createCartItemRequest.getProductId()))
            throw new InternalServerErrorException("Ya existe ese producto en el carrito");
        CartItem cartItem = CartItem.builder()
                .cart(cart).product(product)
                .quantity(createCartItemRequest.getQuantity())
                .createdAt(OffsetDateTime.now()).updatedAt(OffsetDateTime.now())
                .build();
        return CartItemMapper.modelToCartItemResponse(cartItemRepository.save(cartItem));
    }

    /**
     * Actualiza la cantidad y/o el producto de un item de carrito existente.
     * Solo modifica los campos presentes en el request; lanza excepción si
     * el ID es inválido, el item no existe o el nuevo producto no existe.
     */
    @Override
    public CartItemResponse updateCartItem(Integer id, UpdateCartItemRequest req) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Item de carrito no encontrado con el id: %d", id)));
        if (req.getQuantity() != null && req.getQuantity() > 0) cartItem.setQuantity(req.getQuantity());
        if (req.getProductId() != null && req.getProductId() > 0) {
            Product product = productRepository.findById(req.getProductId())
                    .orElseThrow(() -> new NotFoundException("El producto no existe"));
            cartItem.setProduct(product);
        }
        cartItem.setUpdatedAt(OffsetDateTime.now());
        return CartItemMapper.modelToCartItemResponse(cartItemRepository.save(cartItem));
    }

    /**
     * Elimina un CartItem existente por su ID.
     * Lanza excepción si el ID es inválido o si el CartItem no existe.
     */
    @Override
    public DeleteCartItemResponse deleteCartItem(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("CartItem no encontrado con el id: %d", id)));
        cartItemRepository.delete(cartItem);
        return new DeleteCartItemResponse("CartItem con id " + id + " eliminado correctamente");
    }
}
