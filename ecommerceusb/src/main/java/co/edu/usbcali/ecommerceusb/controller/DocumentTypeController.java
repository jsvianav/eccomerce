package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreateDocumentTypeRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteDocumentTypeResponse;
import co.edu.usbcali.ecommerceusb.dto.DocumentTypeResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateDocumentTypeRequest;
import co.edu.usbcali.ecommerceusb.service.DocumentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST para la gestión de tipos de documento
@RestController
@RequestMapping("/documentType")
public class DocumentTypeController {

    // Inyección del servicio que contiene la lógica de negocio de DocumentType
    @Autowired
    private DocumentTypeService documentTypeService;

    // Retorna todos los tipos de documento registrados
    @GetMapping
    public ResponseEntity<List<DocumentTypeResponse>> getDocumentTypes() {
        return ResponseEntity.ok(documentTypeService.getDocumentTypes());
    }

    // Busca un tipo de documento por su ID; retorna 400 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<?> getDocumentTypeById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(documentTypeService.getDocumentTypeById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Crea un nuevo tipo de documento con los datos del body
    @PostMapping
    public ResponseEntity<?> createDocumentType(@RequestBody CreateDocumentTypeRequest createDocumentTypeRequest) {
        try {
            return ResponseEntity.ok(documentTypeService.createDocumentType(createDocumentTypeRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Actualiza un tipo de documento existente por su ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDocumentType(@PathVariable Integer id, @RequestBody UpdateDocumentTypeRequest updateDocumentTypeRequest) {
        try {
            return ResponseEntity.ok(documentTypeService.updateDocumentType(id, updateDocumentTypeRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteDocumentTypeResponse> deleteDocumentType(
            @PathVariable Integer id) throws Exception {
        return new ResponseEntity<>(documentTypeService.deleteDocumentType(id), HttpStatus.OK);
    }
}
