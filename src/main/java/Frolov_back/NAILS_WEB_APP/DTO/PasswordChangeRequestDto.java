package Frolov_back.NAILS_WEB_APP.DTO;

import lombok.Data;

@Data
public class PasswordChangeRequestDto {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
