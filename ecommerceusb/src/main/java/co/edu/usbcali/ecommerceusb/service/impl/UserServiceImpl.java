package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateUserRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteUserResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateUserRequest;
import co.edu.usbcali.ecommerceusb.dto.UserResponse;
import co.edu.usbcali.ecommerceusb.exception.BadRequestException;
import co.edu.usbcali.ecommerceusb.exception.InternalServerErrorException;
import co.edu.usbcali.ecommerceusb.exception.NotFoundException;
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
        if (users.isEmpty()) return List.of();
        return UserMapper.modelToUserResponse(users);
    }

    /**
     * Busca y retorna un usuario específico por su ID.
     * Lanza una excepción si el ID es inválido o si el usuario no existe.
     */
    @Override
    public UserResponse getUserById(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar el id para buscar");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Usuario no encontrado con el id: %d", id)));
        return UserMapper.modelToUserResponse(user);
    }

    /**
     * Busca y retorna un usuario específico por su email.
     * Lanza una excepción si el email está vacío o si el usuario no existe.
     */
    @Override
    public UserResponse getUserByEmail(String email) {
        if (email == null || email.isBlank()) throw new BadRequestException("Debe ingresar email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(String.format("Usuario no encontrado con el email: %s", email)));
        return UserMapper.modelToUserResponse(user);
    }

    /**
     * Crea un nuevo usuario en la base de datos.
     * Valida que todos los campos obligatorios estén presentes, que el tipo de documento
     * exista, y que no haya duplicados por email ni por documento+tipo de documento.
     */
    @Override
    public UserResponse createUser(CreateUserRequest req) {
        if (Objects.isNull(req))
            throw new BadRequestException("El objeto createUserRequest no puede ser nulo");
        if (Objects.isNull(req.getFullName()) || req.getFullName().isBlank())
            throw new BadRequestException("El campo fullName no puede ser nulo ni vacío");
        if (Objects.isNull(req.getPhone()) || req.getPhone().isBlank())
            throw new BadRequestException("El campo phone no puede ser nulo ni vacío");
        if (Objects.isNull(req.getEmail()) || req.getEmail().isBlank())
            throw new BadRequestException("El campo email no puede ser nulo ni vacío");
        if (req.getDocumentTypeId() == null || req.getDocumentTypeId() <= 0)
            throw new BadRequestException("El campo documentTypeId debe contener un valor mayor a 0");
        if (Objects.isNull(req.getDocumentNumber()) || req.getDocumentNumber().isBlank())
            throw new BadRequestException("El campo documentNumber no puede estar nulo ni vacío");
        if (Objects.isNull(req.getBirthDate()) || req.getBirthDate().isBlank())
            throw new BadRequestException("El campo birthDate no puede estar nulo ni vacío");
        if (Objects.isNull(req.getCountry()) || req.getCountry().isBlank())
            throw new BadRequestException("El campo country no puede estar nulo ni vacío");
        if (Objects.isNull(req.getAddress()) || req.getAddress().isBlank())
            throw new BadRequestException("El campo address no puede estar nulo ni vacío");
        DocumentType documentType = documentTypeRepository.findById(req.getDocumentTypeId())
                .orElseThrow(() -> new NotFoundException("El tipo de documento no existe"));
        // Duplicados → InternalServerErrorException
        if (userRepository.existsByEmail(req.getEmail()))
            throw new InternalServerErrorException("Ya existe un usuario con el email ingresado");
        if (userRepository.existsByDocumentNumberAndDocumentTypeId(req.getDocumentNumber(), req.getDocumentTypeId()))
            throw new InternalServerErrorException("Ya existe un usuario con el documento y tipo de documento ingresados");
        User user = UserMapper.createUserRequestToUser(req, documentType);
        userRepository.save(user);
        return UserMapper.modelToUserResponse(user);
    }

    /**
     * Actualiza los datos de un usuario existente identificado por su ID.
     * Solo modifica los campos presentes en el request; valida unicidad de email
     * si este cambia, y verifica que el tipo de documento exista si se actualiza.
     */
    @Override
    public UserResponse updateUser(Integer id, UpdateUserRequest req) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Usuario no encontrado con el id: %d", id)));
        if (req.getFullName() != null && !req.getFullName().isBlank()) user.setFullName(req.getFullName());
        if (req.getPhone() != null && !req.getPhone().isBlank()) user.setPhone(req.getPhone());
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            if (!req.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(req.getEmail()))
                throw new InternalServerErrorException("Ya existe un usuario con el email ingresado");
            user.setEmail(req.getEmail());
        }
        if (req.getDocumentTypeId() != null && req.getDocumentTypeId() > 0) {
            DocumentType documentType = documentTypeRepository.findById(req.getDocumentTypeId())
                    .orElseThrow(() -> new NotFoundException("El tipo de documento no existe"));
            user.setDocumentType(documentType);
        }
        if (req.getDocumentNumber() != null && !req.getDocumentNumber().isBlank()) user.setDocumentNumber(req.getDocumentNumber());
        if (req.getBirthDate() != null && !req.getBirthDate().isBlank())
            user.setBirthDate(LocalDate.parse(req.getBirthDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        if (req.getCountry() != null && !req.getCountry().isBlank()) user.setCountry(req.getCountry());
        if (req.getAddress() != null && !req.getAddress().isBlank()) user.setAddress(req.getAddress());
        userRepository.save(user);
        return UserMapper.modelToUserResponse(user);
    }

    /**
     * Elimina un User existente por su ID.
     * Lanza excepción si el ID es inválido o si el User no existe.
     */
    @Override
    public DeleteUserResponse deleteUser(Integer id) {
        if (id == null || id <= 0) throw new BadRequestException("Debe ingresar un id válido");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User no encontrado con el id: %d", id)));
        userRepository.delete(user);
        return new DeleteUserResponse("User con id " + id + " eliminado correctamente");
    }
}
