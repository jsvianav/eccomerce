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

    @Autowired
    private DocumentTypeService documentTypeService;

    // Retorna todos los tipos de documento registrados
    @GetMapping
    public ResponseEntity<List<DocumentTypeResponse>> getDocumentTypes() {
        return ResponseEntity.ok(documentTypeService.getDocumentTypes());
    }

    // Busca un tipo de documento por su ID
    @GetMapping("/{id}")
    public ResponseEntity<DocumentTypeResponse> getDocumentTypeById(@PathVariable Integer id) {
        return ResponseEntity.ok(documentTypeService.getDocumentTypeById(id));
    }

    // Crea un nuevo tipo de documento con los datos del body. Retorna 201 Created.
    @PostMapping
    public ResponseEntity<DocumentTypeResponse> createDocumentType(@RequestBody CreateDocumentTypeRequest createDocumentTypeRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentTypeService.createDocumentType(createDocumentTypeRequest));
    }

    // Actualiza un tipo de documento existente por su ID
    @PutMapping("/{id}")
    public ResponseEntity<DocumentTypeResponse> updateDocumentType(@PathVariable Integer id, @RequestBody UpdateDocumentTypeRequest updateDocumentTypeRequest) {
        return ResponseEntity.ok(documentTypeService.updateDocumentType(id, updateDocumentTypeRequest));
    }

    // Elimina un tipo de documento por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteDocumentTypeResponse> deleteDocumentType(@PathVariable Integer id) {
        return ResponseEntity.ok(documentTypeService.deleteDocumentType(id));
    }
}
