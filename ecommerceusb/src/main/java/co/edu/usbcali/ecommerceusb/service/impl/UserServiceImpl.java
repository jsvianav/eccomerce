package co.edu.usbcali.ecommerceusb.service.impl;
import co.edu.usbcali.ecommerceusb.dto.CreateUserRequest;
import co.edu.usbcali.ecommerceusb.dto.UserResponse;
import co.edu.usbcali.ecommerceusb.mapper.UserMapper;
import co.edu.usbcali.ecommerceusb.model.DocumentType;
import co.edu.usbcali.ecommerceusb.model.User;
import co.edu.usbcali.ecommerceusb.repository.DocumentTypeRepository;
import co.edu.usbcali.ecommerceusb.repository.UserRepository;
import co.edu.usbcali.ecommerceusb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    @Override
    public List<UserResponse> getUsers() {
        List<User> users = userRepository.findAll(); // ← agregado

        if (users.isEmpty()) {
            return List.of();
        }

        List<UserResponse> userResponses = UserMapper.modelToUserResponse(users);
        return userResponses;
    }
    @Override
    public UserResponse getUserById(Integer id) throws Exception {

        if (id == null || id <= 0) {
            throw new Exception("Debe ingresar el id para buscar");
        }


        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new Exception(
                                String.format("Usuario no encontrado con el id: %d", id)));


        UserResponse userResponse = UserMapper.modelToUserResponse(user);

        return userResponse;
    }
    @Override
    public UserResponse getUserByEmail(String email) throws Exception {

        if(email == null || email.isBlank()){
            throw new Exception("Debe ingresar email");
        }

        User userByEmail = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new Exception(
                                String.format("Usuario no encontrado con el email: %s", email)));

        return UserMapper.modelToUserResponse(userByEmail);
    }
    @Override
    public UserResponse createUser(CreateUserRequest createUserRequest) throws Exception {

        // Validar que el campo fullName no sea nulo ni vacío
        if (Objects.isNull(createUserRequest.getFullName()) ||
                createUserRequest.getFullName().isBlank()) {
            throw new Exception("El campo fullName no puede ser nulo ni vacío");
        }

        // Validar que el campo phone no sea nulo ni vacío
        if (Objects.isNull(createUserRequest.getPhone()) ||
                createUserRequest.getPhone().isBlank()) {
            throw new Exception("El campo phone no puede ser nulo ni vacío");
        }

        // Validar que el campo email no sea nulo ni vacío
        if (Objects.isNull(createUserRequest.getEmail()) ||
                createUserRequest.getEmail().isBlank()) {
            throw new Exception("El campo email no puede ser nulo ni vacío");
        }

        // Validar que el campo documentTypeId no sea nulo ni <= 0
        if (createUserRequest.getDocumentTypeId() == null ||
                createUserRequest.getDocumentTypeId() <= 0) {
            throw new Exception("El campo documentTypeId debe contener un valor mayor a 0");
        }

        // Validar campo documentNumber no sea nulo ni vacío
        if (Objects.isNull(createUserRequest.getDocumentNumber()) ||
                createUserRequest.getDocumentNumber().isBlank()) {
            throw new Exception("El campo documentNumber no puede estar nulo ni vacío");
        }

        // Validar campo birthDate
        if (Objects.isNull(createUserRequest.getBirthDate()) ||
                createUserRequest.getBirthDate().isBlank()) {
            throw new Exception("El campo birthDate no puede estar nulo ni vacío");
        }

        // Validar campo country
        if (Objects.isNull(createUserRequest.getCountry()) ||
                createUserRequest.getCountry().isBlank()) {
            throw new Exception("El campo country no puede estar nulo ni vacío");
        }

        // Validar campo address
        if (Objects.isNull(createUserRequest.getAddress()) ||
                createUserRequest.getAddress().isBlank()) {
            throw new Exception("El campo address no puede estar nulo ni vacío");
        }

        // Validar que existe el document type
        DocumentType documentType = documentTypeRepository.findById(createUserRequest.getDocumentTypeId())
                .orElseThrow(() -> new Exception("El tipo de documento no existe"));

        // Validar que no exista un usuario creado con el mismo email
        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new Exception("Ya existe un usuario con el email ingresado");
        }

        // Validar que no exista un usuario creado con el mismo documento y tipo de documento
        if (userRepository.existsByDocumentNumberAndDocumentTypeId(
                createUserRequest.getDocumentNumber(), createUserRequest.getDocumentTypeId())) {
            throw new Exception("Ya existe un usuario con el documento y tipo de documento ingresados");
        }

        User user = UserMapper.createUserRequestToUser(createUserRequest, documentType);
        userRepository.save(user);
        UserResponse userResponse = UserMapper.modelToUserResponse(user);
        return userResponse;


    }
}
