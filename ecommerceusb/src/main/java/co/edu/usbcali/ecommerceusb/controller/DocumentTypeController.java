package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.DocumentTypeResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateDocumentTypeRequest;
import co.edu.usbcali.ecommerceusb.service.DocumentTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "DocumentType", description = "Operaciones para la gestión de tipos de documento")
public class DocumentTypeController {

    // Inyección del servicio que contiene la lógica de negocio de tipos de documento
    @Autowired
    private DocumentTypeService documentTypeService;

    /**
     * Retorna la lista completa de tipos de documento disponibles.
     */
    @Operation(summary = "Listar todos los tipos de documento", description = "Retorna todos los tipos de documento registrados en el sistema")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<DocumentTypeResponse>> getDocumentTypes() {
        return ResponseEntity.ok(documentTypeService.getDocumentTypes());
    }

    /**
     * Busca un tipo de documento por su ID.
     */
    @Operation(summary = "Buscar tipo de documento por ID", description = "Retorna un tipo de documento específico según su identificador")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tipo de documento encontrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o tipo de documento no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getDocumentTypeById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(documentTypeService.getDocumentTypeById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualiza el nombre y/o código de un tipo de documento existente.
     */
    @Operation(summary = "Actualizar tipo de documento", description = "Modifica el nombre o código de un tipo de documento existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tipo de documento actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o datos incorrectos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDocumentType(@PathVariable Integer id, @RequestBody UpdateDocumentTypeRequest updateDocumentTypeRequest) {
        try {
            return ResponseEntity.ok(documentTypeService.updateDocumentType(id, updateDocumentTypeRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
