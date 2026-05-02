package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreateUserRequest;
import co.edu.usbcali.ecommerceusb.dto.UserResponse;
import co.edu.usbcali.ecommerceusb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest createUserRequest) {
        try {
            return ResponseEntity.ok(userService.createUser(createUserRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
