package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreatePaymentRequest;
import co.edu.usbcali.ecommerceusb.dto.PaymentResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdatePaymentRequest;
import co.edu.usbcali.ecommerceusb.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getPayments() {
        return ResponseEntity.ok(paymentService.getPayments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(paymentService.getPaymentById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody CreatePaymentRequest createPaymentRequest) {
        try {
            return ResponseEntity.ok(paymentService.createPayment(createPaymentRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePayment(@PathVariable Integer id, @RequestBody UpdatePaymentRequest updatePaymentRequest) {
        try {
            return ResponseEntity.ok(paymentService.updatePayment(id, updatePaymentRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
