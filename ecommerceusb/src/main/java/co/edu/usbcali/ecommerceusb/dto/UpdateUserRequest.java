package co.edu.usbcali.ecommerceusb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    private String fullName;
    private String phone;
    private String email;
    private Integer documentTypeId;
    private String documentNumber;
    private String birthDate;
    private String country;
    private String address;
}
