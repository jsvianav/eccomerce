package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreatePaymentRequest;
import co.edu.usbcali.ecommerceusb.dto.DeletePaymentResponse;
import co.edu.usbcali.ecommerceusb.dto.PaymentResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdatePaymentRequest;
import co.edu.usbcali.ecommerceusb.mapper.PaymentMapper;
import co.edu.usbcali.ecommerceusb.model.Order;
import co.edu.usbcali.ecommerceusb.model.Payment;
import co.edu.usbcali.ecommerceusb.model.Payment.PaymentStatus;
import co.edu.usbcali.ecommerceusb.repository.OrderRepository;
import co.edu.usbcali.ecommerceusb.repository.PaymentRepository;
import co.edu.usbcali.ecommerceusb.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Retorna la lista de todos los pagos registrados en la base de datos.
     * Si no existe ningún pago, retorna una lista vacía.
     */
    @Override
    public List<PaymentResponse> getPayments() {
        List<Payment> payments = paymentRepository.findAll();
        // Si no hay pagos, retorna lista vacía en lugar de null
        if (payments.isEmpty()) return List.of();
        // Convierte la lista de entidades al formato de respuesta
        return PaymentMapper.modelToPaymentResponseList(payments);
    }

    /**
     * Busca y retorna un pago específico por su ID.
     * Lanza una excepción si el ID es inválido o si el pago no existe.
     */
    @Override
    public PaymentResponse getPaymentById(Integer id) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar el id para buscar");
        // Busca el pago; lanza excepción si no se encuentra
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Pago no encontrado con el id: %d", id)));
        // Convierte la entidad al objeto de respuesta y lo retorna
        return PaymentMapper.modelToPaymentResponse(payment);
    }

    /**
     * Crea un nuevo pago en la base de datos asociado a una orden existente.
     * Valida que el request no sea nulo, que orderId, status e idempotencyKey sean válidos,
     * que la orden exista, y que no haya otro pago con la misma clave de idempotencia.
     * La clave de idempotencia garantiza que un mismo pago no se procese más de una vez.
     */
    @Override
    public PaymentResponse createPayment(CreatePaymentRequest createPaymentRequest) throws Exception {
        // Valida que el objeto request no sea nulo para evitar NullPointerException
        if (Objects.isNull(createPaymentRequest))
            throw new Exception("El objeto createPaymentRequest no puede ser nulo");
        // Valida que el orderId no sea nulo y sea mayor a 0
        if (Objects.isNull(createPaymentRequest.getOrderId()) || createPaymentRequest.getOrderId() <= 0)
            throw new Exception("El campo orderId debe contener un valor mayor a 0");
        // Valida que el campo status no esté vacío ni nulo
        if (Objects.isNull(createPaymentRequest.getStatus()) || createPaymentRequest.getStatus().isBlank())
            throw new Exception("El campo status no puede estar nulo ni vacío");
        // Intenta convertir el String recibido al enum PaymentStatus (SUCCEEDED, FAILED).
        // valueOf() lanza IllegalArgumentException si el valor no corresponde a ningún valor del enum,
        // por eso se envuelve en un try-catch para retornar un mensaje de error claro al usuario.
        PaymentStatus paymentStatus;
        try {
            paymentStatus = PaymentStatus.valueOf(createPaymentRequest.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new Exception("El status debe ser SUCCEEDED o FAILED");
        }
        // Valida que la clave de idempotencia no esté vacía ni nula
        if (Objects.isNull(createPaymentRequest.getIdempotencyKey()) || createPaymentRequest.getIdempotencyKey().isBlank())
            throw new Exception("El campo idempotencyKey no puede estar nulo ni vacío");
        // Verifica que la orden exista en la base de datos
        Order order = orderRepository.findById(createPaymentRequest.getOrderId())
                .orElseThrow(() -> new Exception("La orden no existe"));
        // Verifica que no exista ya un pago con la misma idempotencyKey, evitando pagos duplicados
        if (paymentRepository.existsByIdempotencyKey(createPaymentRequest.getIdempotencyKey()))
            throw new Exception("Ya existe un pago con el idempotencyKey ingresado");
        // Construye la entidad Payment con los datos validados y la marca de tiempo actual
        Payment payment = Payment.builder()
                .order(order).status(paymentStatus)
                .providerRef(createPaymentRequest.getProviderRef())
                .idempotencyKey(createPaymentRequest.getIdempotencyKey())
                .createdAt(OffsetDateTime.now())
                .build();
        // Guarda el pago en la base de datos y retorna la respuesta mapeada
        return PaymentMapper.modelToPaymentResponse(paymentRepository.save(payment));
    }

    /**
     * Actualiza el estado y/o referencia del proveedor de un pago existente.
     * Solo modifica los campos presentes en el request; lanza excepción si
     * el ID es inválido, el pago no existe o el status no es un valor permitido.
     */
    @Override
    public PaymentResponse updatePayment(Integer id, UpdatePaymentRequest req) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        // Busca el pago; lanza excepción si no se encuentra
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Pago no encontrado con el id: %d", id)));
        // Actualiza el status si viene en el request, validando que sea SUCCEEDED o FAILED
        if (req.getStatus() != null && !req.getStatus().isBlank()) {
            // Intenta convertir el String al enum PaymentStatus; si no es válido lanza excepción con mensaje claro
            try {
                payment.setStatus(PaymentStatus.valueOf(req.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new Exception("El status debe ser SUCCEEDED o FAILED");
            }
        }
        // Actualiza la referencia del proveedor de pago si viene en el request y no está vacía
        if (req.getPaymentMethod() != null && !req.getPaymentMethod().isBlank())
            payment.setProviderRef(req.getPaymentMethod());
        // Guarda los cambios en la base de datos y retorna la respuesta mapeada
        return PaymentMapper.modelToPaymentResponse(paymentRepository.save(payment));
    }
    /**
     * Elimina un Payment existente por su ID.
     * Lanza excepción si el ID es inválido o si el Payment no existe.
     */
    @Override
    public DeletePaymentResponse deletePayment(Integer id) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        // Busca el Payment; lanza excepción si no se encuentra
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Payment no encontrado con el id: %d", id)));
        // Elimina el registro de la base de datos
        paymentRepository.delete(payment);
        // Retorna la respuesta con mensaje de confirmación
        return new DeletePaymentResponse("Payment con id " + id + " eliminado correctamente");
    }
}
