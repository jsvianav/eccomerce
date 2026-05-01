package co.edu.usbcali.ecommerceusb.mapper;

import co.edu.usbcali.ecommerceusb.dto.CartResponse;
import co.edu.usbcali.ecommerceusb.model.Cart;

import java.util.List;

public class CartMapper {

    public static CartResponse modelToCartResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUser() != null ? cart.getUser().getId() : null)
                .status(cart.getStatus() != null ? cart.getStatus().name() : null)
                .build();
    }

    public static List<CartResponse> modelToCartResponseList(List<Cart> carts) {
        return carts.stream().map(CartMapper::modelToCartResponse).toList();
    }
}
