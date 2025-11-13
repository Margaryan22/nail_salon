package Frolov_back.NAILS_WEB_APP.DTO;

import lombok.Data;

import java.time.LocalDateTime;

// --- DTO для ответа с информацией о пользователе ---
@Data
public class UserResponseDto {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private LocalDateTime createdAt;
    private String userType; // "ADMIN", "MASTER", "CLIENT"
    private String role; // Из enum UserRoleType
}
