package co.edu.usbcali.ecommerceusb.advice;

import co.edu.usbcali.ecommerceusb.dto.ApiErrorResponse;
import co.edu.usbcali.ecommerceusb.exception.BadRequestException;
import co.edu.usbcali.ecommerceusb.exception.InternalServerErrorException;
import co.edu.usbcali.ecommerceusb.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            NotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiErrorResponse(404, "Not Found", ex.getMessage(), request.getRequestURI())
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            BadRequestException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiErrorResponse(400, "Bad Request", ex.getMessage(), request.getRequestURI())
        );
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ApiErrorResponse> handleInternalServerError(
            InternalServerErrorException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiErrorResponse(500, "Internal Server Error", ex.getMessage(), request.getRequestURI())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiErrorResponse(400, "Bad Request", msg, request.getRequestURI())
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiErrorResponse(400, "Bad Request", ex.getMessage(), request.getRequestURI())
        );
    }

    /**
     * Captura violaciones de constraints de base de datos y devuelve mensajes
     * de error legibles en lugar de un 500 genérico.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        String rootMsg = ex.getRootCause() != null ? ex.getRootCause().getMessage() : "";
        String message;
        int httpStatus;

        if (rootMsg.contains("price_check")) {
            message = "El precio del producto no puede ser negativo";
            httpStatus = 400;
        } else if (rootMsg.contains("uq_one_success_payment_per_order")) {
            message = "Ya existe un pago exitoso registrado para esta orden";
            httpStatus = 409;
        } else if (rootMsg.contains("unique constraint") || rootMsg.contains("duplicate key")) {
            message = "Ya existe un registro con los datos ingresados";
            httpStatus = 409;
        } else if (rootMsg.contains("check constraint") || rootMsg.contains("violates check")) {
            message = "Los datos ingresados no cumplen las restricciones del sistema";
            httpStatus = 400;
        } else if (rootMsg.contains("foreign key") || rootMsg.contains("violates foreign key")) {
            message = "No se puede realizar la operación: referencia a un registro inexistente";
            httpStatus = 400;
        } else {
            message = "Operación no permitida por restricción de datos";
            httpStatus = 409;
        }

        String errorLabel = httpStatus == 400 ? "Bad Request" : "Conflict";
        return ResponseEntity.status(httpStatus).body(
                new ApiErrorResponse(httpStatus, errorLabel, message, request.getRequestURI())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiErrorResponse(500, "Internal Server Error",
                        "Error interno del servidor", request.getRequestURI())
        );
    }
}
