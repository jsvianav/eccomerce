package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreateUserRequest;
import co.edu.usbcali.ecommerceusb.dto.UpdateUserRequest;
import co.edu.usbcali.ecommerceusb.dto.UserResponse;
import co.edu.usbcali.ecommerceusb.service.UserService;
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
public class UserController {

    // Inyección del servicio que contiene la lógica de negocio de usuarios
    @Autowired
    private UserService userService;

    /**
     * Retorna la lista completa de usuarios registrados.
     * No requiere parámetros. Siempre retorna 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers() {
        // Delega al servicio y envuelve el resultado en un 200 OK
        return ResponseEntity.ok(userService.getUsers());
    }

    /**
     * Busca un usuario por su ID.
     * Retorna 200 OK si existe, o 400 Bad Request si el ID es inválido o no se encuentra.
     */
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
     * Valida unicidad de email y de número de documento antes de persistir.
     * Retorna 200 OK con el usuario creado, o 400 si los datos son inválidos o hay duplicados.
     */
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
     * Solo modifica los campos enviados en el body; valida unicidad de email si este cambia.
     */
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
