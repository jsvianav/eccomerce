package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.DocumentTypeResponse;
import co.edu.usbcali.ecommerceusb.mapper.DocumentTypeMapper;
import co.edu.usbcali.ecommerceusb.model.DocumentType;
import co.edu.usbcali.ecommerceusb.repository.DocumentTypeRepository;
import co.edu.usbcali.ecommerceusb.service.DocumentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentTypeServiceImpl implements DocumentTypeService {

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    @Override
    public List<DocumentTypeResponse> getDocumentTypes() {
        List<DocumentType> documentTypes = documentTypeRepository.findAll();

        if (documentTypes.isEmpty()) {
            return List.of();
        }

        return DocumentTypeMapper.modelToDocumentTypeResponseList(documentTypes);
    }

    @Override
    public DocumentTypeResponse getDocumentTypeById(Integer id) throws Exception {

        if (id == null || id <= 0) {
            throw new Exception("Debe ingresar el id para buscar");
        }

        DocumentType documentType = documentTypeRepository.findById(id)
                .orElseThrow(() ->
                        new Exception(
                                String.format("Tipo de documento no encontrado con el id: %d", id)));

        return DocumentTypeMapper.modelToDocumentTypeResponse(documentType);
    }
}
