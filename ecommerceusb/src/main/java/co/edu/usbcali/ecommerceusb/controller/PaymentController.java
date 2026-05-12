package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreatePaymentRequest;
import co.edu.usbcali.ecommerceusb.dto.PaymentResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdatePaymentRequest;
import co.edu.usbcali.ecommerceusb.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
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

    // Inyección del servicio que contiene la lógica de negocio de pagos
    @Autowired
    private PaymentService paymentService;

    /**
     * Retorna la lista completa de pagos registrados.
     * No requiere parámetros. Siempre retorna 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getPayments() {
        // Delega al servicio y envuelve el resultado en un 200 OK
        return ResponseEntity.ok(paymentService.getPayments());
    }

    /**
     * Busca un pago por su ID.
     * Retorna 200 OK si existe, o 400 Bad Request si el ID es inválido o no se encuentra.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Integer id) {
        try {
            // Intenta buscar el pago; retorna 200 si existe
            return ResponseEntity.ok(paymentService.getPaymentById(id));
        } catch (Exception e) {
            // Si el servicio lanza una excepción, retorna 400 con el mensaje de error
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Registra un nuevo pago asociado a una orden.
     * Usa idempotencyKey para evitar que el mismo pago se procese más de una vez.
     * Retorna 200 OK con el pago creado, o 400 si los datos son inválidos o hay duplicado.
     */
    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody CreatePaymentRequest createPaymentRequest) {
        try {
            // Pasa el request al servicio para que valide la idempotencyKey y persista el pago
            return ResponseEntity.ok(paymentService.createPayment(createPaymentRequest));
        } catch (Exception e) {
            // Retorna 400 con el mensaje de validación si algo falla
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualiza el estado o referencia del proveedor de un pago existente.
     * Solo modifica los campos enviados en el body.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePayment(@PathVariable Integer id, @RequestBody UpdatePaymentRequest updatePaymentRequest) {
        try {
            // Pasa el id y el request al servicio para que actualice solo los campos recibidos
            return ResponseEntity.ok(paymentService.updatePayment(id, updatePaymentRequest));
        } catch (Exception e) {
            // Retorna 400 con el mensaje de error si la operación falla
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
