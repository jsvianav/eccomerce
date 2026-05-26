package co.edu.usbcali.ecommerceusb.service;

import co.edu.usbcali.ecommerceusb.dto.CreatePaymentRequest;
import co.edu.usbcali.ecommerceusb.dto.DeletePaymentResponse;
import co.edu.usbcali.ecommerceusb.dto.PaymentResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdatePaymentRequest;
import java.util.List;

public interface PaymentService {
    List<PaymentResponse> getPayments();
    PaymentResponse getPaymentById(Integer id);
    PaymentResponse createPayment(CreatePaymentRequest createPaymentRequest);
    PaymentResponse updatePayment(Integer id, UpdatePaymentRequest updatePaymentRequest);
    DeletePaymentResponse deletePayment(Integer id);
}
