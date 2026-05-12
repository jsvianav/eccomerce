package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.DocumentTypeResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateDocumentTypeRequest;
import co.edu.usbcali.ecommerceusb.service.DocumentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de tipos de documento (Cédula, Pasaporte, etc.).
 * Expone endpoints de consulta y actualización bajo la ruta base /documentType.
 * No expone creación porque los tipos de documento son datos maestros precargados.
 */
@RestController
@RequestMapping("/documentType")
public class DocumentTypeController {

    // Inyección del servicio que contiene la lógica de negocio de tipos de documento
    @Autowired
    private DocumentTypeService documentTypeService;

    /**
     * Retorna la lista completa de tipos de documento disponibles.
     * No requiere parámetros. Siempre retorna 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<DocumentTypeResponse>> getDocumentTypes() {
        // Delega al servicio y envuelve el resultado en un 200 OK
        return ResponseEntity.ok(documentTypeService.getDocumentTypes());
    }

    /**
     * Busca un tipo de documento por su ID.
     * Retorna 200 OK si existe, o 400 Bad Request si el ID es inválido o no se encuentra.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDocumentTypeById(@PathVariable Integer id) {
        try {
            // Intenta buscar el tipo de documento; retorna 200 si existe
            return ResponseEntity.ok(documentTypeService.getDocumentTypeById(id));
        } catch (Exception e) {
            // Si el servicio lanza una excepción, retorna 400 con el mensaje de error
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualiza el nombre y/o código de un tipo de documento existente.
     * Solo modifica los campos enviados en el body.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDocumentType(@PathVariable Integer id, @RequestBody UpdateDocumentTypeRequest updateDocumentTypeRequest) {
        try {
            // Pasa el id y el request al servicio para que actualice solo los campos recibidos
            return ResponseEntity.ok(documentTypeService.updateDocumentType(id, updateDocumentTypeRequest));
        } catch (Exception e) {
            // Retorna 400 con el mensaje de error si la operación falla
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
