package co.edu.usbcali.ecommerceusb.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String resource, Integer id) {
        super(String.format("%s no encontrado con el id: %d", resource, id));
    }

    public NotFoundException(String resource, String field, String value) {
        super(String.format("%s no encontrado con %s: %s", resource, field, value));
    }
}