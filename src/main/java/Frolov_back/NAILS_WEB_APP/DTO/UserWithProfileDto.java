package Frolov_back.NAILS_WEB_APP.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserWithProfileDto {
    // Основная информация пользователя
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private LocalDateTime createdAt;
    private String role;

    // Профиль (только один будет заполнен)
    private AdminProfileDto adminProfile;
    private ClientProfileDto clientProfile;
    private MasterProfileDto masterProfile;

}