package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateDocumentTypeRequest;
import co.edu.usbcali.ecommerceusb.dto.DocumentTypeResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateDocumentTypeRequest;
import co.edu.usbcali.ecommerceusb.mapper.DocumentTypeMapper;
import co.edu.usbcali.ecommerceusb.model.DocumentType;
import co.edu.usbcali.ecommerceusb.repository.DocumentTypeRepository;
import co.edu.usbcali.ecommerceusb.service.DocumentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class DocumentTypeServiceImpl implements DocumentTypeService {

    // Repositorio para acceder a la tabla document_types en la base de datos
    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    /**
     * Retorna la lista de todos los tipos de documento registrados en la base de datos.
     * Si no existe ningún tipo de documento, retorna una lista vacía.
     */
    @Override
    public List<DocumentTypeResponse> getDocumentTypes() {
        List<DocumentType> documentTypes = documentTypeRepository.findAll();
        // Si no hay tipos de documento, retorna lista vacía en lugar de null
        if (documentTypes.isEmpty()) return List.of();
        // Convierte la lista de entidades al formato de respuesta
        return DocumentTypeMapper.modelToDocumentTypeResponseList(documentTypes);
    }

    /**
     * Busca y retorna un tipo de documento específico por su ID.
     * Lanza una excepción si el ID es inválido o si el tipo de documento no existe.
     */
    @Override
    public DocumentTypeResponse getDocumentTypeById(Integer id) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar el id para buscar");
        // Busca el tipo de documento; lanza excepción si no se encuentra
        DocumentType documentType = documentTypeRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Tipo de documento no encontrado con el id: %d", id)));
        // Convierte la entidad al objeto de respuesta y lo retorna
        return DocumentTypeMapper.modelToDocumentTypeResponse(documentType);
    }

    /**
     * Crea un nuevo tipo de documento en la base de datos.
     * Valida que el request no sea nulo y que el campo name no esté vacío.
     */
    @Override
    public DocumentTypeResponse createDocumentType(CreateDocumentTypeRequest req) throws Exception {
        // Valida que el objeto request no sea nulo
        if (Objects.isNull(req))
            throw new Exception("El objeto createDocumentTypeRequest no puede ser nulo");
        // Valida que el campo name no sea nulo ni vacío
        if (Objects.isNull(req.getName()) || req.getName().isBlank())
            throw new Exception("El campo name no puede ser nulo ni vacío");
        // Convierte el request al modelo DocumentType usando el mapper
        DocumentType documentType = DocumentTypeMapper.createDocumentTypeRequestToDocumentType(req);
        // Guarda el nuevo tipo de documento en la base de datos
        documentTypeRepository.save(documentType);
        // Retorna la respuesta mapeada del tipo de documento creado
        return DocumentTypeMapper.modelToDocumentTypeResponse(documentType);
    }

    /**
     * Actualiza el nombre y/o código de un tipo de documento existente.
     * Solo modifica los campos presentes en el request; lanza excepción si
     * el ID es inválido o el tipo de documento no existe.
     */
    @Override
    public DocumentTypeResponse updateDocumentType(Integer id, UpdateDocumentTypeRequest req) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        // Busca el tipo de documento; lanza excepción si no se encuentra
        DocumentType documentType = documentTypeRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Tipo de documento no encontrado con el id: %d", id)));
        // Actualiza el nombre solo si viene en el request y no está vacío
        if (req.getName() != null && !req.getName().isBlank()) documentType.setName(req.getName());
        // Actualiza el código solo si viene en el request y no está vacío
        if (req.getCode() != null && !req.getCode().isBlank()) documentType.setCode(req.getCode());
        // Guarda los cambios en la base de datos
        documentTypeRepository.save(documentType);
        // Retorna la respuesta mapeada del tipo de documento actualizado
        return DocumentTypeMapper.modelToDocumentTypeResponse(documentType);
    }
}
