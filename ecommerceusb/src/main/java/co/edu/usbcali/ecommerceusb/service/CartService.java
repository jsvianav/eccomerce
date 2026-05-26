package co.edu.usbcali.ecommerceusb.service;

import co.edu.usbcali.ecommerceusb.dto.CartResponse;
import co.edu.usbcali.ecommerceusb.dto.CreateCartRequest;
import co.edu.usbcali.ecommerceusb.dto.UpdateCartRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteCartResponse;
import java.util.List;

public interface CartService {
    List<CartResponse> getCarts();
    CartResponse getCartById(Integer id);
    CartResponse createCart(CreateCartRequest createCartRequest);
    CartResponse updateCart(Integer id, UpdateCartRequest updateCartRequest);
    DeleteCartResponse deleteCart(Integer id);
}
