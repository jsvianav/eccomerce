package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreatePaymentRequest;
import co.edu.usbcali.ecommerceusb.dto.DeletePaymentResponse;
import co.edu.usbcali.ecommerceusb.dto.PaymentResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdatePaymentRequest;
import co.edu.usbcali.ecommerceusb.exception.BadRequestException;
import co.edu.usbcali.ecommerceusb.exception.InternalServerErrorException;
import co.edu.usbcali.ecommerceusb.exception.NotFoundException;
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
        if (payments.isEmpty()) return List.of();
        return PaymentMapper.modelToPaymentResponseList(payments);
    }

    /**
     * Busca y retorna un pago específico por su ID.
     * Lanza una excepción si el ID es inválido o si el pago no existe.
     */
    @Override
    public PaymentResponse getPaymentById(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar el id para buscar");
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Pago no encontrado con el id: %d", id)));
        return PaymentMapper.modelToPaymentResponse(payment);
    }

    /**
     * Crea un nuevo pago en la base de datos asociado a una orden existente.
     * Valida que el request no sea nulo, que orderId, status e idempotencyKey sean válidos,
     * que la orden exista, y que no haya otro pago con la misma clave de idempotencia.
     * La clave de idempotencia garantiza que un mismo pago no se procese más de una vez.
     */
    @Override
    public PaymentResponse createPayment(CreatePaymentRequest createPaymentRequest) {
        if (Objects.isNull(createPaymentRequest))
            throw new BadRequestException("El objeto createPaymentRequest no puede ser nulo");
        if (Objects.isNull(createPaymentRequest.getOrderId()) || createPaymentRequest.getOrderId() <= 0)
            throw new BadRequestException("El campo orderId debe contener un valor mayor a 0");
        if (Objects.isNull(createPaymentRequest.getStatus()) || createPaymentRequest.getStatus().isBlank())
            throw new BadRequestException("El campo status no puede estar nulo ni vacío");
        // valueOf() lanza IllegalArgumentException si el valor no corresponde a ningún valor del enum
        PaymentStatus paymentStatus;
        try {
            paymentStatus = PaymentStatus.valueOf(createPaymentRequest.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("El status debe ser SUCCEEDED o FAILED");
        }
        if (Objects.isNull(createPaymentRequest.getIdempotencyKey()) || createPaymentRequest.getIdempotencyKey().isBlank())
            throw new BadRequestException("El campo idempotencyKey no puede estar nulo ni vacío");
        Order order = orderRepository.findById(createPaymentRequest.getOrderId())
                .orElseThrow(() -> new NotFoundException("La orden no existe"));
        // idempotencyKey duplicada → BadRequestException (idempotencia: misma clave = mismo pago)
        if (paymentRepository.existsByIdempotencyKey(createPaymentRequest.getIdempotencyKey()))
            throw new BadRequestException("Ya existe un pago registrado con esta clave de idempotencia");
        Payment payment = Payment.builder()
                .order(order).status(paymentStatus)
                .providerRef(createPaymentRequest.getProviderRef())
                .idempotencyKey(createPaymentRequest.getIdempotencyKey())
                .createdAt(OffsetDateTime.now())
                .build();
        return PaymentMapper.modelToPaymentResponse(paymentRepository.save(payment));
    }

    /**
     * Actualiza el estado y/o referencia del proveedor de un pago existente.
     * Solo modifica los campos presentes en el request; lanza excepción si
     * el ID es inválido, el pago no existe o el status no es un valor permitido.
     */
    @Override
    public PaymentResponse updatePayment(Integer id, UpdatePaymentRequest req) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Pago no encontrado con el id: %d", id)));
        if (req.getStatus() != null && !req.getStatus().isBlank()) {
            try {
                payment.setStatus(PaymentStatus.valueOf(req.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("El status debe ser SUCCEEDED o FAILED");
            }
        }
        if (req.getProviderRef() != null && !req.getProviderRef().isBlank())
            payment.setProviderRef(req.getProviderRef());
        return PaymentMapper.modelToPaymentResponse(paymentRepository.save(payment));
    }

    /**
     * Elimina un Payment existente por su ID.
     * Lanza excepción si el ID es inválido o si el Payment no existe.
     */
    @Override
    public DeletePaymentResponse deletePayment(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Payment no encontrado con el id: %d", id)));
        paymentRepository.delete(payment);
        return new DeletePaymentResponse("Payment con id " + id + " eliminado correctamente");
    }
}
