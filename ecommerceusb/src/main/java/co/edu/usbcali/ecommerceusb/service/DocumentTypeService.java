package co.edu.usbcali.ecommerceusb.service;

import co.edu.usbcali.ecommerceusb.dto.CreateDocumentTypeRequest;
import co.edu.usbcali.ecommerceusb.dto.DocumentTypeResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateDocumentTypeRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteDocumentTypeResponse;
import java.util.List;

// Interfaz de servicio para la entidad DocumentType
public interface DocumentTypeService {

    // Retorna todos los tipos de documento registrados
    List<DocumentTypeResponse> getDocumentTypes();

    // Busca un tipo de documento por su ID
    DocumentTypeResponse getDocumentTypeById(Integer id);

    // Crea un nuevo tipo de documento con los datos del request
    DocumentTypeResponse createDocumentType(CreateDocumentTypeRequest createDocumentTypeRequest);

    // Actualiza un tipo de documento existente por su ID
    DocumentTypeResponse updateDocumentType(Integer id, UpdateDocumentTypeRequest updateDocumentTypeRequest);

    DeleteDocumentTypeResponse deleteDocumentType(Integer id);
}
