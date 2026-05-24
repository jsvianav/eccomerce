package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CartResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCartRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteCartResponse;
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

    /**
     * Retorna la lista de todos los carritos registrados en la base de datos.
     * Si no existe ningún carrito, retorna una lista vacía.
     */
    @Override
    public List<CartResponse> getCarts() {
        List<Cart> carts = cartRepository.findAll();
        // Si no hay carritos en la base de datos, se retorna lista vacía en lugar de null
        if (carts.isEmpty()) return List.of();
        // Convierte la lista de entidades Cart a una lista de objetos de respuesta CartResponse
        return CartMapper.modelToCartResponseList(carts);
    }

    /**
     * Busca y retorna un carrito específico por su ID.
     * Lanza una excepción si el ID es inválido o si el carrito no existe.
     */
    @Override
    public CartResponse getCartById(Integer id) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar el id para buscar");
        // Busca el carrito en la base de datos; lanza excepción si no se encuentra
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Carrito no encontrado con el id: %d", id)));
        // Convierte la entidad Cart al objeto de respuesta y lo retorna
        return CartMapper.modelToCartResponse(cart);
    }

    /**
     * Crea un nuevo carrito en la base de datos a partir de los datos del request.
     * Valida que el request contenga un userId válido y un status permitido,
     * verifica que el usuario exista y persiste el nuevo carrito.
     */
    @Override
    public CartResponse createCart(CreateCartRequest req) throws Exception {
        // Valida que el objeto request no sea nulo para evitar NullPointerException en las siguientes validaciones
        if (Objects.isNull(req))
            throw new Exception("El objeto createCartRequest no puede ser nulo");
        // Valida que el userId no sea nulo y sea mayor a 0
        if (Objects.isNull(req.getUserId()) || req.getUserId() <= 0)
            throw new Exception("El campo userId debe contener un valor mayor a 0");
        // Valida que el campo status no esté vacío ni nulo
        if (Objects.isNull(req.getStatus()) || req.getStatus().isBlank())
            throw new Exception("El campo status no puede estar nulo ni vac\u00edo");
        // Intenta convertir el string del status al enum CartStatus; lanza excepción si no es un valor válido
        CartStatus cartStatus;
        try {
            cartStatus = CartStatus.valueOf(req.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new Exception("El status debe ser ACTIVE, CHECKED_OUT o ABANDONED");
        }
        // Verifica que el usuario exista en la base de datos
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new Exception("El usuario no existe"));
        // Construye la entidad Cart con los datos validados y las marcas de tiempo actuales
        Cart cart = Cart.builder()
                .user(user).status(cartStatus)
                .createdAt(OffsetDateTime.now()).updatedAt(OffsetDateTime.now())
                .build();
        // Guarda el carrito en la base de datos y retorna la respuesta mapeada
        return CartMapper.modelToCartResponse(cartRepository.save(cart));
    }

    /**
     * Actualiza el estado de un carrito existente identificado por su ID.
     * Solo actualiza los campos presentes en el request; lanza excepción si
     * el ID es inválido, el carrito no existe o el status no es un valor permitido.
     */
    @Override
    public CartResponse updateCart(Integer id, UpdateCartRequest req) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id v\u00e1lido");
        // Busca el carrito en la base de datos; lanza excepción si no se encuentra
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Carrito no encontrado con el id: %d", id)));
        // Si se envió un status en el request, lo valida y actualiza en la entidad
        if (req.getStatus() != null && !req.getStatus().isBlank()) {
            try {
                // Convierte el string al enum CartStatus; lanza excepción si no es un valor permitido
                cart.setStatus(CartStatus.valueOf(req.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new Exception("El status debe ser ACTIVE, CHECKED_OUT o ABANDONED");
            }
        }
        // Actualiza la marca de tiempo de la última modificación
        cart.setUpdatedAt(OffsetDateTime.now());
        // Guarda los cambios y retorna la respuesta mapeada
        return CartMapper.modelToCartResponse(cartRepository.save(cart));
    }
    /**
     * Elimina un carrito existente por su ID.
     * Lanza excepción si el ID es inválido o si el carrito no existe.
     */
    @Override
    public DeleteCartResponse deleteCart(Integer id) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        // Busca el carrito; lanza excepción si no se encuentra
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Carrito no encontrado con el id: %d", id)));
        // Elimina el registro de la base de datos
        cartRepository.delete(cart);
        // Retorna la respuesta con mensaje de confirmación
        return new DeleteCartResponse("Carrito con id " + id + " eliminado correctamente");
    }
}
