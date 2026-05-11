package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CartItemResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCartItemRequest;
import co.edu.usbcali.ecommerceusb.dto.UpdateCartItemRequest;
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
        if (cartItems.isEmpty()) return List.of();
        return CartItemMapper.modelToCartItemResponseList(cartItems);
    }

    @Override
    public CartItemResponse getCartItemById(Integer id) throws Exception {
        if (id == null || id <= 0) throw new Exception("Debe ingresar el id para buscar");
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Item de carrito no encontrado con el id: %d", id)));
        return CartItemMapper.modelToCartItemResponse(cartItem);
    }

    @Override
    public CartItemResponse createCartItem(CreateCartItemRequest createCartItemRequest) throws Exception {
        if (Objects.isNull(createCartItemRequest.getCartId()) || createCartItemRequest.getCartId() <= 0)
            throw new Exception("El campo cartId debe contener un valor mayor a 0");
        if (Objects.isNull(createCartItemRequest.getProductId()) || createCartItemRequest.getProductId() <= 0)
            throw new Exception("El campo productId debe contener un valor mayor a 0");
        if (Objects.isNull(createCartItemRequest.getQuantity()) || createCartItemRequest.getQuantity() <= 0)
            throw new Exception("El campo quantity debe ser mayor a 0");
        Cart cart = cartRepository.findById(createCartItemRequest.getCartId())
                .orElseThrow(() -> new Exception("El carrito no existe"));
        Product product = productRepository.findById(createCartItemRequest.getProductId())
                .orElseThrow(() -> new Exception("El producto no existe"));
        if (cartItemRepository.existsByCartIdAndProductId(createCartItemRequest.getCartId(), createCartItemRequest.getProductId()))
            throw new Exception("Ya existe ese producto en el carrito");
        CartItem cartItem = CartItem.builder()
                .cart(cart).product(product)
                .quantity(createCartItemRequest.getQuantity())
                .createdAt(OffsetDateTime.now()).updatedAt(OffsetDateTime.now())
                .build();
        return CartItemMapper.modelToCartItemResponse(cartItemRepository.save(cartItem));
    }

    @Override
    public CartItemResponse updateCartItem(Integer id, UpdateCartItemRequest req) throws Exception {
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Item de carrito no encontrado con el id: %d", id)));
        if (req.getQuantity() != null && req.getQuantity() > 0) cartItem.setQuantity(req.getQuantity());
        if (req.getProductId() != null && req.getProductId() > 0) {
            Product product = productRepository.findById(req.getProductId())
                    .orElseThrow(() -> new Exception("El producto no existe"));
            cartItem.setProduct(product);
        }
        cartItem.setUpdatedAt(OffsetDateTime.now());
        return CartItemMapper.modelToCartItemResponse(cartItemRepository.save(cartItem));
    }
}
