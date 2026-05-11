package co.edu.usbcali.ecommerceusb.service;

import co.edu.usbcali.ecommerceusb.dto.CartItemResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCartItemRequest;
import co.edu.usbcali.ecommerceusb.dto.UpdateCartItemRequest;

import java.util.List;

public interface CartItemService {
    List<CartItemResponse> getCartItems();
    CartItemResponse getCartItemById(Integer id) throws Exception;
    CartItemResponse createCartItem(CreateCartItemRequest createCartItemRequest) throws Exception;
    CartItemResponse updateCartItem(Integer id, UpdateCartItemRequest updateCartItemRequest) throws Exception;
}
