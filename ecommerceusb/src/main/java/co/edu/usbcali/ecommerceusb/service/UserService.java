package co.edu.usbcali.ecommerceusb.service;

import co.edu.usbcali.ecommerceusb.dto.CreateUserRequest;
import co.edu.usbcali.ecommerceusb.dto.DeleteUserResponse;
import co.edu.usbcali.ecommerceusb.dto.UpdateUserRequest;
import co.edu.usbcali.ecommerceusb.dto.UserResponse;
import java.util.List;

public interface UserService {
    List<UserResponse> getUsers();
    UserResponse getUserById(Integer id);
    UserResponse getUserByEmail(String email);
    UserResponse createUser(CreateUserRequest createUserRequest);
    UserResponse updateUser(Integer id, UpdateUserRequest updateUserRequest);
    DeleteUserResponse deleteUser(Integer id);
}