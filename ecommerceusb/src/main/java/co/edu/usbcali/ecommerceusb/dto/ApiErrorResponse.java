package co.edu.usbcali.ecommerceusb.dto;

import lombok.Getter;
import java.time.OffsetDateTime;

@Getter
public class ApiErrorResponse {

    private final OffsetDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;

    public ApiErrorResponse(int status, String error, String message, String path) {
        this.timestamp = OffsetDateTime.now();
        this.status    = status;
        this.error     = error;
        this.message   = message;
        this.path      = path;
    }
}