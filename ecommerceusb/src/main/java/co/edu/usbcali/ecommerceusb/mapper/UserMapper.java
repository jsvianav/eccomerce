package co.edu.usbcali.ecommerceusb.mapper;

import co.edu.usbcali.ecommerceusb.dto.UserResponse;
import co.edu.usbcali.ecommerceusb.model.DocumentType;
import co.edu.usbcali.ecommerceusb.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserResponse modelToUserResponse(User user) {
        DocumentType documentType = user.getDocumentType();

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .documentTypeId(documentType == null ? null : documentType.getId())
                .documentTypeName(documentType == null ? null : documentType.getName())
                .documentNumber(user.getDocumentNumber())
                .build();
    }

    public static List<UserResponse> modelToUserResponse(List<User> users) {
        return users.stream().map(UserMapper::modelToUserResponse).toList();
    }
}