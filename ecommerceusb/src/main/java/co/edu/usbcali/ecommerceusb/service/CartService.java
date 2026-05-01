package co.edu.usbcali.ecommerceusb.service;

import co.edu.usbcali.ecommerceusb.dto.CartResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCartRequest;

import java.util.List;

public interface CartService {

    List<CartResponse> getCarts();

    CartResponse getCartById(Integer id) throws Exception;

    CartResponse createCart(CreateCartRequest createCartRequest) throws Exception;
}
