package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreateUserRequest;
import co.edu.usbcali.ecommerceusb.dto.UpdateUserRequest;
import co.edu.usbcali.ecommerceusb.dto.UserResponse;
import co.edu.usbcali.ecommerceusb.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de usuarios.
 * Expone los endpoints CRUD bajo la ruta base /user.
 */
@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "Operaciones para la gestión de usuarios del ecommerce")
public class UserController {

    // Inyección del servicio que contiene la lógica de negocio de usuarios
    @Autowired
    private UserService userService;

    /**
     * Retorna la lista completa de usuarios registrados.
     */
    @Operation(summary = "Listar todos los usuarios", description = "Retorna todos los usuarios registrados en el sistema")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    /**
     * Busca un usuario por su ID.
     */
    @Operation(summary = "Buscar usuario por ID", description = "Retorna un usuario específico según su identificador")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        try {
            // Intenta buscar el usuario; retorna 200 si existe
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (Exception e) {
            // Si el servicio lanza una excepción, retorna 400 con el mensaje de error
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Crea un nuevo usuario en el sistema.
     * Valida unicidad de email y de número de documento.
     */
    @Operation(summary = "Crear un usuario", description = "Registra un nuevo usuario; valida que el email y documento no estén duplicados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario duplicado")
    })
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest createUserRequest) {
        try {
            // Pasa el request al servicio para que lo valide y persista
            return ResponseEntity.ok(userService.createUser(createUserRequest));
        } catch (Exception e) {
            // Retorna 400 con el mensaje de validación si algo falla
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualiza los datos de un usuario existente.
     * Solo modifica los campos enviados en el body.
     */
    @Operation(summary = "Actualizar un usuario", description = "Modifica los datos personales de un usuario existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "ID inválido o datos incorrectos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody UpdateUserRequest updateUserRequest) {
        try {
            // Pasa el id y el request al servicio para que actualice solo los campos recibidos
            return ResponseEntity.ok(userService.updateUser(id, updateUserRequest));
        } catch (Exception e) {
            // Retorna 400 con el mensaje de error si la operación falla
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
