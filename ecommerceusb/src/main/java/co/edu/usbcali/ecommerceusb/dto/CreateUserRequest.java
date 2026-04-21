package co.edu.usbcali.ecommerceusb.dto;


import lombok.AllArgsConstructor;

@AllArgsConstructor
@lombok.Getter
public class CreateUserRequest {
    private String fullName;
    private String email;
    private String phone;
    private String documentNumber;
    private Integer documentTypeId;
    private String birthDate;
    private String country;
    private String address;
}
