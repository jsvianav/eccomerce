package co.edu.usbcali.ecommerceusb.mapper;

import co.edu.usbcali.ecommerceusb.dto.CartItemResponse;
import co.edu.usbcali.ecommerceusb.model.CartItem;

import java.util.List;

public class CartItemMapper {

    public static CartItemResponse modelToCartItemResponse(CartItem cartItem) {
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .cartId(cartItem.getCart() != null ? cartItem.getCart().getId() : null)
                .productId(cartItem.getProduct() != null ? cartItem.getProduct().getId() : null)
                .quantity(cartItem.getQuantity())
                .build();
    }

    public static List<CartItemResponse> modelToCartItemResponseList(List<CartItem> cartItems) {
        return cartItems.stream().map(CartItemMapper::modelToCartItemResponse).toList();
    }
}
