package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreatePaymentRequest;
import co.edu.usbcali.ecommerceusb.dto.DeletePaymentResponse;
import co.edu.usbcali.ecommerceusb.dto.PaymentResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdatePaymentRequest;
import co.edu.usbcali.ecommerceusb.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de pagos.
 * Expone los endpoints CRUD bajo la ruta base /payment.
 */
@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /** Retorna la lista completa de pagos registrados. */
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getPayments() {
        return ResponseEntity.ok(paymentService.getPayments());
    }

    /** Busca un pago por su ID. */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Integer id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    /**
     * Registra un nuevo pago asociado a una orden.
     * Usa idempotencyKey para evitar que el mismo pago se procese más de una vez. Retorna 201 Created.
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody CreatePaymentRequest createPaymentRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPayment(createPaymentRequest));
    }

    /** Actualiza el estado o referencia del proveedor de un pago existente. */
    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponse> updatePayment(@PathVariable Integer id, @RequestBody UpdatePaymentRequest updatePaymentRequest) {
        return ResponseEntity.ok(paymentService.updatePayment(id, updatePaymentRequest));
    }

    /** Elimina un pago por su ID. */
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletePaymentResponse> deletePayment(@PathVariable Integer id) {
        return ResponseEntity.ok(paymentService.deletePayment(id));
    }
}
