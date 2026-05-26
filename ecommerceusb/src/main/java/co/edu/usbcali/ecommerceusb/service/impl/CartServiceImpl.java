package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CartResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCartRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteCartResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateCartRequest;
import co.edu.usbcali.ecommerceusb.exception.BadRequestException;
import co.edu.usbcali.ecommerceusb.exception.NotFoundException;
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

    /**
     * Retorna la lista de todos los carritos registrados en la base de datos.
     * Si no existe ningún carrito, retorna una lista vacía.
     */
    @Override
    public List<CartResponse> getCarts() {
        List<Cart> carts = cartRepository.findAll();
        if (carts.isEmpty()) return List.of();
        return CartMapper.modelToCartResponseList(carts);
    }

    /**
     * Busca y retorna un carrito específico por su ID.
     * Lanza una excepción si el ID es inválido o si el carrito no existe.
     */
    @Override
    public CartResponse getCartById(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar el id para buscar");
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Carrito no encontrado con el id: %d", id)));
        return CartMapper.modelToCartResponse(cart);
    }

    /**
     * Crea un nuevo carrito en la base de datos a partir de los datos del request.
     * Valida que el request contenga un userId válido y un status permitido,
     * verifica que el usuario exista y persiste el nuevo carrito.
     */
    @Override
    public CartResponse createCart(CreateCartRequest req) {
        if (Objects.isNull(req))
            throw new BadRequestException("El objeto createCartRequest no puede ser nulo");
        if (Objects.isNull(req.getUserId()) || req.getUserId() <= 0)
            throw new BadRequestException("El campo userId debe contener un valor mayor a 0");
        if (Objects.isNull(req.getStatus()) || req.getStatus().isBlank())
            throw new BadRequestException("El campo status no puede estar nulo ni vacío");
        CartStatus cartStatus;
        try {
            cartStatus = CartStatus.valueOf(req.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("El status debe ser ACTIVE, CHECKED_OUT o ABANDONED");
        }
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new NotFoundException("El usuario no existe"));
        Cart cart = Cart.builder()
                .user(user).status(cartStatus)
                .createdAt(OffsetDateTime.now()).updatedAt(OffsetDateTime.now())
                .build();
        return CartMapper.modelToCartResponse(cartRepository.save(cart));
    }

    /**
     * Actualiza el estado de un carrito existente identificado por su ID.
     * Solo actualiza los campos presentes en el request; lanza excepción si
     * el ID es inválido, el carrito no existe o el status no es un valor permitido.
     */
    @Override
    public CartResponse updateCart(Integer id, UpdateCartRequest req) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Carrito no encontrado con el id: %d", id)));
        if (req.getStatus() != null && !req.getStatus().isBlank()) {
            try {
                cart.setStatus(CartStatus.valueOf(req.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("El status debe ser ACTIVE, CHECKED_OUT o ABANDONED");
            }
        }
        cart.setUpdatedAt(OffsetDateTime.now());
        return CartMapper.modelToCartResponse(cartRepository.save(cart));
    }

    /**
     * Elimina un carrito existente por su ID.
     * Lanza excepción si el ID es inválido o si el carrito no existe.
     */
    @Override
    public DeleteCartResponse deleteCart(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Carrito no encontrado con el id: %d", id)));
        cartRepository.delete(cart);
        return new DeleteCartResponse("Carrito con id " + id + " eliminado correctamente");
    }
}
