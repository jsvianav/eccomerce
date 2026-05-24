package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CartItemResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCartItemRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteCartItemResponse;
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

    /**
     * Retorna la lista de todos los items de carrito registrados en la base de datos.
     * Si no existe ningún item, retorna una lista vacía.
     */
    @Override
    public List<CartItemResponse> getCartItems() {
        List<CartItem> cartItems = cartItemRepository.findAll();
        // Si no hay items en la base de datos, se retorna lista vacía en lugar de null
        if (cartItems.isEmpty()) return List.of();
        // Convierte la lista de entidades CartItem a lista de objetos de respuesta CartItemResponse
        return CartItemMapper.modelToCartItemResponseList(cartItems);
    }

    /**
     * Busca y retorna un item de carrito específico por su ID.
     * Lanza una excepción si el ID es inválido o si el item no existe.
     */
    @Override
    public CartItemResponse getCartItemById(Integer id) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar el id para buscar");
        // Busca el item en la base de datos; lanza excepción si no se encuentra
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Item de carrito no encontrado con el id: %d", id)));
        // Convierte la entidad al objeto de respuesta y lo retorna
        return CartItemMapper.modelToCartItemResponse(cartItem);
    }

    /**
     * Crea un nuevo item en un carrito existente.
     * Valida que el request no sea nulo, que cartId, productId y quantity sean válidos,
     * que el carrito y el producto existan, y que el producto no esté ya en ese carrito.
     */
    @Override
    public CartItemResponse createCartItem(CreateCartItemRequest createCartItemRequest) throws Exception {
        // Valida que el objeto request no sea nulo
        if (Objects.isNull(createCartItemRequest))
            throw new Exception("El objeto createCartItemRequest no puede ser nulo");
        // Valida que el cartId no sea nulo y sea mayor a 0
        if (Objects.isNull(createCartItemRequest.getCartId()) || createCartItemRequest.getCartId() <= 0)
            throw new Exception("El campo cartId debe contener un valor mayor a 0");
        // Valida que el productId no sea nulo y sea mayor a 0
        if (Objects.isNull(createCartItemRequest.getProductId()) || createCartItemRequest.getProductId() <= 0)
            throw new Exception("El campo productId debe contener un valor mayor a 0");
        // Valida que la cantidad sea mayor a 0
        if (Objects.isNull(createCartItemRequest.getQuantity()) || createCartItemRequest.getQuantity() <= 0)
            throw new Exception("El campo quantity debe ser mayor a 0");
        // Verifica que el carrito exista en la base de datos
        Cart cart = cartRepository.findById(createCartItemRequest.getCartId())
                .orElseThrow(() -> new Exception("El carrito no existe"));
        // Verifica que el producto exista en la base de datos
        Product product = productRepository.findById(createCartItemRequest.getProductId())
                .orElseThrow(() -> new Exception("El producto no existe"));
        // Verifica que el producto no esté ya agregado en ese carrito (evita duplicados)
        if (cartItemRepository.existsByCartIdAndProductId(createCartItemRequest.getCartId(), createCartItemRequest.getProductId()))
            throw new Exception("Ya existe ese producto en el carrito");
        // Construye el item con los datos validados y las marcas de tiempo actuales
        CartItem cartItem = CartItem.builder()
                .cart(cart).product(product)
                .quantity(createCartItemRequest.getQuantity())
                .createdAt(OffsetDateTime.now()).updatedAt(OffsetDateTime.now())
                .build();
        // Guarda el item en la base de datos y retorna la respuesta mapeada
        return CartItemMapper.modelToCartItemResponse(cartItemRepository.save(cartItem));
    }

    /**
     * Actualiza la cantidad y/o el producto de un item de carrito existente.
     * Solo modifica los campos presentes en el request; lanza excepción si
     * el ID es inválido, el item no existe o el nuevo producto no existe.
     */
    @Override
    public CartItemResponse updateCartItem(Integer id, UpdateCartItemRequest req) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        // Busca el item en la base de datos; lanza excepción si no se encuentra
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Item de carrito no encontrado con el id: %d", id)));
        // Si se envió una nueva cantidad válida, la actualiza
        if (req.getQuantity() != null && req.getQuantity() > 0) cartItem.setQuantity(req.getQuantity());
        // Si se envió un nuevo productId, verifica que exista y actualiza la referencia
        if (req.getProductId() != null && req.getProductId() > 0) {
            Product product = productRepository.findById(req.getProductId())
                    .orElseThrow(() -> new Exception("El producto no existe"));
            cartItem.setProduct(product);
        }
        // Actualiza la marca de tiempo de última modificación
        cartItem.setUpdatedAt(OffsetDateTime.now());
        // Guarda los cambios y retorna la respuesta mapeada
        return CartItemMapper.modelToCartItemResponse(cartItemRepository.save(cartItem));
    }
    /**
     * Elimina un CartItem existente por su ID.
     * Lanza excepción si el ID es inválido o si el CartItem no existe.
     */
    @Override
    public DeleteCartItemResponse deleteCartItem(Integer id) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        // Busca el CartItem; lanza excepción si no se encuentra
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("CartItem no encontrado con el id: %d", id)));
        // Elimina el registro de la base de datos
        cartItemRepository.delete(cartItem);
        // Retorna la respuesta con mensaje de confirmación
        return new DeleteCartItemResponse("CartItem con id " + id + " eliminado correctamente");
    }
}
