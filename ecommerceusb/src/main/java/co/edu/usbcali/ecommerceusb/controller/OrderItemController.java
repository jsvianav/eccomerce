package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreateOrderItemRequest;
import co.edu.usbcali.ecommerceusb.dto.OrderItemResponse;
import co.edu.usbcali.ecommerceusb.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orderItem")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    @GetMapping
    public ResponseEntity<List<OrderItemResponse>> getOrderItems() {
        return ResponseEntity.ok(orderItemService.getOrderItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemResponse> getOrderItemById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(orderItemService.getOrderItemById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<OrderItemResponse> createOrderItem(
            @RequestBody CreateOrderItemRequest createOrderItemRequest) {
        try {
            return ResponseEntity.ok(orderItemService.createOrderItem(createOrderItemRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
