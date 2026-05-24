package co.edu.usbcali.ecommerceusb.service;

import co.edu.usbcali.ecommerceusb.dto.CreatePaymentRequest;
import co.edu.usbcali.ecommerceusb.dto.DeletePaymentResponse;
import co.edu.usbcali.ecommerceusb.dto.PaymentResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdatePaymentRequest;

import java.util.List;

public interface PaymentService {
    List<PaymentResponse> getPayments();
    PaymentResponse getPaymentById(Integer id) throws Exception;
    PaymentResponse createPayment(CreatePaymentRequest createPaymentRequest) throws Exception;
    PaymentResponse updatePayment(Integer id, UpdatePaymentRequest updatePaymentRequest) throws Exception;
    DeletePaymentResponse deletePayment(Integer id) throws Exception;
}
