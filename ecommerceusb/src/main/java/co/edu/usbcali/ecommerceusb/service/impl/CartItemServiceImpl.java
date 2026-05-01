package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CartItemResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCartItemRequest;
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

    @Override
    public List<CartItemResponse> getCartItems() {
        List<CartItem> cartItems = cartItemRepository.findAll();

        if (cartItems.isEmpty()) {
            return List.of();
        }

        return CartItemMapper.modelToCartItemResponseList(cartItems);
    }

    @Override
    public CartItemResponse getCartItemById(Integer id) throws Exception {

        if (id == null || id <= 0) {
            throw new Exception("Debe ingresar el id para buscar");
        }

        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() ->
                        new Exception(
                                String.format("Item de carrito no encontrado con el id: %d", id)));

        return CartItemMapper.modelToCartItemResponse(cartItem);
    }

    @Override
    public CartItemResponse createCartItem(CreateCartItemRequest createCartItemRequest) throws Exception {

        // Validar campo cartId
        if (Objects.isNull(createCartItemRequest.getCartId()) ||
                createCartItemRequest.getCartId() <= 0) {
            throw new Exception("El campo cartId debe contener un valor mayor a 0");
        }

        // Validar campo productId
        if (Objects.isNull(createCartItemRequest.getProductId()) ||
                createCartItemRequest.getProductId() <= 0) {
            throw new Exception("El campo productId debe contener un valor mayor a 0");
        }

        // Validar campo quantity
        if (Objects.isNull(createCartItemRequest.getQuantity()) ||
                createCartItemRequest.getQuantity() <= 0) {
            throw new Exception("El campo quantity debe ser mayor a 0");
        }

        // Validar que el carrito existe
        Cart cart = cartRepository.findById(createCartItemRequest.getCartId())
                .orElseThrow(() -> new Exception("El carrito no existe"));

        // Validar que el producto existe
        Product product = productRepository.findById(createCartItemRequest.getProductId())
                .orElseThrow(() -> new Exception("El producto no existe"));

        // Validar que no exista ya ese producto en el carrito
        if (cartItemRepository.existsByCartIdAndProductId(
                createCartItemRequest.getCartId(), createCartItemRequest.getProductId())) {
            throw new Exception("Ya existe ese producto en el carrito");
        }

        // Construir y guardar el item
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(createCartItemRequest.getQuantity())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        CartItem savedCartItem = cartItemRepository.save(cartItem);

        return CartItemMapper.modelToCartItemResponse(savedCartItem);
    }
}
