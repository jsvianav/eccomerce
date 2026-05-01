package co.edu.usbcali.ecommerceusb.service;

import co.edu.usbcali.ecommerceusb.dto.CartItemResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCartItemRequest;

import java.util.List;

public interface CartItemService {

    List<CartItemResponse> getCartItems();

    CartItemResponse getCartItemById(Integer id) throws Exception;

    CartItemResponse createCartItem(CreateCartItemRequest createCartItemRequest) throws Exception;
}
