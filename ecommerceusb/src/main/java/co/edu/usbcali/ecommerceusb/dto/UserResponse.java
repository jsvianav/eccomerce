package co.edu.usbcali.ecommerceusb.dto;

import lombok.Builder;
import lombok.Getter;
@Builder
@Getter

public class UserResponse {
    private Integer id;
    private String fullName;
    private String email;
    private Integer documentTypeId;
    private String documentTypeName;
    private String documentNumber;


}
