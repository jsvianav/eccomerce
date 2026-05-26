package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateDocumentTypeRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteDocumentTypeResponse;
import co.edu.usbcali.ecommerceusb.dto.DocumentTypeResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateDocumentTypeRequest;
import co.edu.usbcali.ecommerceusb.exception.BadRequestException;
import co.edu.usbcali.ecommerceusb.exception.NotFoundException;
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
        if (documentTypes.isEmpty()) return List.of();
        return DocumentTypeMapper.modelToDocumentTypeResponseList(documentTypes);
    }

    /**
     * Busca y retorna un tipo de documento específico por su ID.
     * Lanza una excepción si el ID es inválido o si el tipo de documento no existe.
     */
    @Override
    public DocumentTypeResponse getDocumentTypeById(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar el id para buscar");
        DocumentType documentType = documentTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Tipo de documento no encontrado con el id: %d", id)));
        return DocumentTypeMapper.modelToDocumentTypeResponse(documentType);
    }

    /**
     * Crea un nuevo tipo de documento en la base de datos.
     * Valida que el request no sea nulo y que el campo name no esté vacío.
     */
    @Override
    public DocumentTypeResponse createDocumentType(CreateDocumentTypeRequest req) {
        if (Objects.isNull(req))
            throw new BadRequestException("El objeto createDocumentTypeRequest no puede ser nulo");
        if (Objects.isNull(req.getName()) || req.getName().isBlank())
            throw new BadRequestException("El campo name no puede ser nulo ni vacío");
        DocumentType documentType = DocumentTypeMapper.createDocumentTypeRequestToDocumentType(req);
        documentTypeRepository.save(documentType);
        return DocumentTypeMapper.modelToDocumentTypeResponse(documentType);
    }

    /**
     * Actualiza el nombre y/o código de un tipo de documento existente.
     * Solo modifica los campos presentes en el request; lanza excepción si
     * el ID es inválido o el tipo de documento no existe.
     */
    @Override
    public DocumentTypeResponse updateDocumentType(Integer id, UpdateDocumentTypeRequest req) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        DocumentType documentType = documentTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Tipo de documento no encontrado con el id: %d", id)));
        if (req.getName() != null && !req.getName().isBlank()) documentType.setName(req.getName());
        if (req.getCode() != null && !req.getCode().isBlank()) documentType.setCode(req.getCode());
        documentTypeRepository.save(documentType);
        return DocumentTypeMapper.modelToDocumentTypeResponse(documentType);
    }

    /**
     * Elimina un DocumentType existente por su ID.
     * Lanza excepción si el ID es inválido o si el DocumentType no existe.
     */
    @Override
    public DeleteDocumentTypeResponse deleteDocumentType(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        DocumentType documentType = documentTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("DocumentType no encontrado con el id: %d", id)));
        documentTypeRepository.delete(documentType);
        return new DeleteDocumentTypeResponse("DocumentType con id " + id + " eliminado correctamente");
    }
}
