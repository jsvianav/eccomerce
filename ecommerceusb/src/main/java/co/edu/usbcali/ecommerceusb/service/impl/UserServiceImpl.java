package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateUserRequest;
import co.edu.usbcali.ecommerceusb.dto.UpdateUserRequest;
import co.edu.usbcali.ecommerceusb.dto.UserResponse;
import co.edu.usbcali.ecommerceusb.mapper.UserMapper;
import co.edu.usbcali.ecommerceusb.model.DocumentType;
import co.edu.usbcali.ecommerceusb.model.User;
import co.edu.usbcali.ecommerceusb.repository.DocumentTypeRepository;
import co.edu.usbcali.ecommerceusb.repository.UserRepository;
import co.edu.usbcali.ecommerceusb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    /**
     * Retorna la lista de todos los usuarios registrados en la base de datos.
     * Si no existe ningún usuario, retorna una lista vacía.
     */
    @Override
    public List<UserResponse> getUsers() {
        List<User> users = userRepository.findAll();
        // Si no hay usuarios, retorna lista vacía en lugar de null
        if (users.isEmpty()) return List.of();
        // Convierte la lista de entidades al formato de respuesta
        return UserMapper.modelToUserResponse(users);
    }

    /**
     * Busca y retorna un usuario específico por su ID.
     * Lanza una excepción si el ID es inválido o si el usuario no existe.
     */
    @Override
    public UserResponse getUserById(Integer id) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar el id para buscar");
        // Busca el usuario; lanza excepción si no se encuentra
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Usuario no encontrado con el id: %d", id)));
        // Convierte la entidad al objeto de respuesta y lo retorna
        return UserMapper.modelToUserResponse(user);
    }

    /**
     * Busca y retorna un usuario específico por su email.
     * Lanza una excepción si el email está vacío o si el usuario no existe.
     */
    @Override
    public UserResponse getUserByEmail(String email) throws Exception {
        // Valida que el email no sea nulo ni vacío
        if (email == null || email.isBlank()) throw new Exception("Debe ingresar email");
        // Busca el usuario por email; lanza excepción si no se encuentra
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception(String.format("Usuario no encontrado con el email: %s", email)));
        // Convierte la entidad al objeto de respuesta y lo retorna
        return UserMapper.modelToUserResponse(user);
    }

    /**
     * Crea un nuevo usuario en la base de datos.
     * Valida que todos los campos obligatorios estén presentes, que el tipo de documento
     * exista, y que no haya duplicados por email ni por documento+tipo de documento.
     */
    @Override
    public UserResponse createUser(CreateUserRequest req) throws Exception {
        // Valida que el objeto request no sea nulo
        if (Objects.isNull(req))
            throw new Exception("El objeto createUserRequest no puede ser nulo");
        // Valida que el nombre completo no esté vacío
        if (Objects.isNull(req.getFullName()) || req.getFullName().isBlank())
            throw new Exception("El campo fullName no puede ser nulo ni vacío");
        // Valida que el teléfono no esté vacío
        if (Objects.isNull(req.getPhone()) || req.getPhone().isBlank())
            throw new Exception("El campo phone no puede ser nulo ni vacío");
        // Valida que el email no esté vacío
        if (Objects.isNull(req.getEmail()) || req.getEmail().isBlank())
            throw new Exception("El campo email no puede ser nulo ni vacío");
        // Valida que el documentTypeId sea válido
        if (req.getDocumentTypeId() == null || req.getDocumentTypeId() <= 0)
            throw new Exception("El campo documentTypeId debe contener un valor mayor a 0");
        // Valida que el número de documento no esté vacío
        if (Objects.isNull(req.getDocumentNumber()) || req.getDocumentNumber().isBlank())
            throw new Exception("El campo documentNumber no puede estar nulo ni vacío");
        // Valida que la fecha de nacimiento no esté vacía
        if (Objects.isNull(req.getBirthDate()) || req.getBirthDate().isBlank())
            throw new Exception("El campo birthDate no puede estar nulo ni vacío");
        // Valida que el país no esté vacío
        if (Objects.isNull(req.getCountry()) || req.getCountry().isBlank())
            throw new Exception("El campo country no puede estar nulo ni vacío");
        // Valida que la dirección no esté vacía
        if (Objects.isNull(req.getAddress()) || req.getAddress().isBlank())
            throw new Exception("El campo address no puede estar nulo ni vacío");
        // Verifica que el tipo de documento exista en la base de datos
        DocumentType documentType = documentTypeRepository.findById(req.getDocumentTypeId())
                .orElseThrow(() -> new Exception("El tipo de documento no existe"));
        // Verifica que no exista otro usuario con el mismo email
        if (userRepository.existsByEmail(req.getEmail()))
            throw new Exception("Ya existe un usuario con el email ingresado");
        // Verifica que no exista otro usuario con el mismo número y tipo de documento
        if (userRepository.existsByDocumentNumberAndDocumentTypeId(req.getDocumentNumber(), req.getDocumentTypeId()))
            throw new Exception("Ya existe un usuario con el documento y tipo de documento ingresados");
        // Usa el mapper para construir la entidad User a partir del request
        User user = UserMapper.createUserRequestToUser(req, documentType);
        // Guarda el usuario en la base de datos
        userRepository.save(user);
        // Retorna la respuesta mapeada del usuario creado
        return UserMapper.modelToUserResponse(user);
    }

    /**
     * Actualiza los datos de un usuario existente identificado por su ID.
     * Solo modifica los campos presentes en el request; valida unicidad de email
     * si este cambia, y verifica que el tipo de documento exista si se actualiza.
     */
    @Override
    public UserResponse updateUser(Integer id, UpdateUserRequest req) throws Exception {
        // Valida que el id no sea nulo y sea mayor a 0
        if (id == null || id <= 0) throw new Exception("Debe ingresar un id válido");
        // Busca el usuario; lanza excepción si no se encuentra
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Usuario no encontrado con el id: %d", id)));
        // Actualiza el nombre si viene en el request y no está vacío
        if (req.getFullName() != null && !req.getFullName().isBlank()) user.setFullName(req.getFullName());
        // Actualiza el teléfono si viene en el request y no está vacío
        if (req.getPhone() != null && !req.getPhone().isBlank()) user.setPhone(req.getPhone());
        // Actualiza el email si viene en el request, verificando que no esté en uso por otro usuario
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            if (!req.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(req.getEmail()))
                throw new Exception("Ya existe un usuario con el email ingresado");
            user.setEmail(req.getEmail());
        }
        // Actualiza el tipo de documento si viene en el request, verificando que exista
        if (req.getDocumentTypeId() != null && req.getDocumentTypeId() > 0) {
            DocumentType documentType = documentTypeRepository.findById(req.getDocumentTypeId())
                    .orElseThrow(() -> new Exception("El tipo de documento no existe"));
            user.setDocumentType(documentType);
        }
        // Actualiza el número de documento si viene en el request y no está vacío
        if (req.getDocumentNumber() != null && !req.getDocumentNumber().isBlank()) user.setDocumentNumber(req.getDocumentNumber());
        // Actualiza la fecha de nacimiento si viene en el request, parséndola al formato yyyy-MM-dd
        if (req.getBirthDate() != null && !req.getBirthDate().isBlank())
            user.setBirthDate(LocalDate.parse(req.getBirthDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        // Actualiza el país si viene en el request y no está vacío
        if (req.getCountry() != null && !req.getCountry().isBlank()) user.setCountry(req.getCountry());
        // Actualiza la dirección si viene en el request y no está vacía
        if (req.getAddress() != null && !req.getAddress().isBlank()) user.setAddress(req.getAddress());
        // Guarda los cambios en la base de datos
        userRepository.save(user);
        // Retorna la respuesta mapeada del usuario actualizado
        return UserMapper.modelToUserResponse(user);
    }
}
