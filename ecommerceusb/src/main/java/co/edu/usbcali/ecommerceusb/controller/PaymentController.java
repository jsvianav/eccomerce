package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreatePaymentRequest;
import co.edu.usbcali.ecommerceusb.dto.PaymentResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdatePaymentRequest;
import co.edu.usbcali.ecommerceusb.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Payment", description = "Operaciones para la gestión de pagos de órdenes")
public class PaymentController {

    // Inyección del servicio que contiene la lógica de negocio de pagos
    @Autowired
    private PaymentService paymentService;

    /**
     * Retorna la lista completa de pagos registrados.
     */
    @Operation(summary = "Listar todos los pagos", description = "Retorna todos los pagos registrados en el sistema")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getPayments() {
        return ResponseEntity.ok(paymentService.getPayments());
    }

    /**
     * Busca un pago por su ID.
     */
    @Operation(summary = "Buscar pago por ID", description = "Retorna un pago específico según su identificador")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pago encontrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o pago no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(paymentService.getPaymentById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Registra un nuevo pago asociado a una orden.
     * Usa idempotencyKey para evitar pagos duplicados.
     */
    @Operation(summary = "Registrar un pago", description = "Crea un nuevo pago asociado a una orden; usa idempotencyKey para prevenir duplicados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pago registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o pago duplicado")
    })
    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody CreatePaymentRequest createPaymentRequest) {
        try {
            return ResponseEntity.ok(paymentService.createPayment(createPaymentRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualiza el estado o referencia de un pago existente.
     */
    @Operation(summary = "Actualizar un pago", description = "Modifica el estado o la referencia del proveedor de un pago existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pago actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o datos incorrectos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePayment(@PathVariable Integer id, @RequestBody UpdatePaymentRequest updatePaymentRequest) {
        try {
            return ResponseEntity.ok(paymentService.updatePayment(id, updatePaymentRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
