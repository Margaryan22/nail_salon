package Frolov_back.NAILS_WEB_APP.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Запрос на аутентификацию пользователя в системе")
public class LoginRequest {

    @Schema(
            description = "Email пользователя для входа в систему",
            example = "client@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED,
            pattern = "^[A-Za-z0-9+_.-]+@(.+)$",
            minLength = 5,
            maxLength = 255
    )
    private String email;

    @Schema(
            description = "Пароль пользователя",
            example = "password123",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 6,
            maxLength = 100,
            format = "password"
    )
    private String password;
}