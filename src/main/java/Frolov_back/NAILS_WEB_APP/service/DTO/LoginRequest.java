package Frolov_back.NAILS_WEB_APP.service.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Запрос на аутентификацию пользователя")
public class LoginRequest {
    @Schema(
            description = "Email пользователя",
            example = "client@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;
    @Schema(
            description = "Пароль",
            example = "password123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;

    // Геттеры и сеттеры
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}