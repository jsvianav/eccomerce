package co.edu.usbcali.ecommerceusb.mapper;

import co.edu.usbcali.ecommerceusb.dto.CreateUserRequest;
import co.edu.usbcali.ecommerceusb.dto.UserResponse;
import co.edu.usbcali.ecommerceusb.model.DocumentType;
import co.edu.usbcali.ecommerceusb.model.User;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
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


    public static User createUserRequestToUser(CreateUserRequest createUserRequest, DocumentType documentType) {
        User user = User.builder()
                .fullName(createUserRequest.getFullName())
                .phone(createUserRequest.getPhone())
                .email(createUserRequest.getEmail())
                .documentType(documentType)
                .documentNumber(createUserRequest.getDocumentNumber())
                .birthDate(LocalDate.parse(
                        createUserRequest.getBirthDate(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .country(createUserRequest.getCountry())
                .address(createUserRequest.getAddress())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
        return user;
    }
}