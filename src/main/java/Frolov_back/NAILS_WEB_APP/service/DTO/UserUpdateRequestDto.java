package Frolov_back.NAILS_WEB_APP.service.DTO;

import lombok.Data;

@Data
public class UserUpdateRequestDto {
    private String firstName;
    private String lastName;
    private String phone;
}
