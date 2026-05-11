package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CartResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCartRequest;
import co.edu.usbcali.ecommerceusb.dto.UpdateCartRequest;
import co.edu.usbcali.ecommerceusb.mapper.CartMapper;
import co.edu.usbcali.ecommerceusb.model.Cart;
import co.edu.usbcali.ecommerceusb.model.Cart.CartStatus;
import co.edu.usbcali.ecommerceusb.model.User;
import co.edu.usbcali.ecommerceusb.repository.CartRepository;
import co.edu.usbcali.ecommerceusb.repository.UserRepository;
import co.edu.usbcali.ecommerceusb.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<CartResponse> getCarts() {
        List<Cart> carts = cartRepository.findAll();
        if (carts.isEmpty()) return List.of();
        return CartMapper.modelToCartResponseList(carts);
    }

    @Override
    public CartResponse getCartById(Integer id) throws Exception {
        if (id == null || id <= 0) throw new Exception("Debe ingresar el id para buscar");
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Carrito no encontrado con el id: %d", id)));
        return CartMapper.modelToCartResponse(cart);
    }

    @Override
    public CartResponse createCart(CreateCartRequest req) throws Exception {
        if (Objects.isNull(req.getUserId()) || req.getUserId() <= 0)
            throw new Exception("El campo userId debe contener un valor mayor a 0");
        if (Objects.isNull(req.getStatus()) || req.getStatus().isBlank())
            throw new Exception("El campo status no puede estar nulo ni vacío");
        CartStatus cartStatus;
        try {
            cartStatus = CartStatus.valueOf(req.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new Exception("El status debe ser ACTIVE, CHECKED_OUT o ABANDONED");
        }
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new Exception("El usuario no existe"));
        Cart cart = Cart.builder()
                .user(user)
                .status(cartStatus)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
        Cart savedCart = cartRepository.save(cart);
        return CartMapper.modelToCartResponse(savedCart);
    }

    @Override
    public CartResponse updateCart(Integer id, UpdateCartRequest req) throws Exception {
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Carrito no encontrado con el id: %d", id)));
        if (req.getStatus() != null && !req.getStatus().isBlank()) {
            try {
                cart.setStatus(CartStatus.valueOf(req.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new Exception("El status debe ser ACTIVE, CHECKED_OUT o ABANDONED");
            }
        }
        cart.setUpdatedAt(OffsetDateTime.now());
        cartRepository.save(cart);
        return CartMapper.modelToCartResponse(cart);
    }
}
