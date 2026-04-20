package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.DocumentTypeResponse;
import co.edu.usbcali.ecommerceusb.service.DocumentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/document-type")
public class DocumentTypeController {

    @Autowired
    private DocumentTypeService documentTypeService;

    @GetMapping("/all")
    public List<DocumentTypeResponse> getAll() {
        return documentTypeService.getDocumentTypes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentTypeResponse> getById(@PathVariable Integer id) throws Exception {
        return new ResponseEntity<>(
                documentTypeService.getDocumentTypeById(id),
                HttpStatus.OK
        );
    }
}
