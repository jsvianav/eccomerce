package co.edu.usbcali.ecommerceusb.service.impl;
import co.edu.usbcali.ecommerceusb.dto.UserResponse;
import co.edu.usbcali.ecommerceusb.mapper.UserMapper;
import co.edu.usbcali.ecommerceusb.model.User;
import co.edu.usbcali.ecommerceusb.repository.UserRepository;
import co.edu.usbcali.ecommerceusb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;


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
    } }