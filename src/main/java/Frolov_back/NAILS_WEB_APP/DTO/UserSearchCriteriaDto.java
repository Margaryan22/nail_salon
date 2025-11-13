package Frolov_back.NAILS_WEB_APP.DTO;

import lombok.Data;

@Data
public class UserSearchCriteriaDto {
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String role;
    private Boolean activeOnly;
}