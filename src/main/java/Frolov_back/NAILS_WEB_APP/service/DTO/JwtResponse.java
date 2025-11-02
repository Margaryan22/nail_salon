package Frolov_back.NAILS_WEB_APP.service.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Ответ системы с JWT токенами для доступа к защищенным ресурсам")
public class JwtResponse {

    @Schema(
            description = "Access Token для доступа к защищенным endpoint'ам API",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJjbGllbnRAZXhhbXBsZS5jb20iLCJpYXQiOjE2MDAwMDAwMDAsImV4cCI6MTYwMDAwMzYwMH0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String accessToken;

    @Schema(
            description = "Refresh Token для обновления Access Token без повторного входа",
            example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String refreshToken;

    @Schema(
            description = "Тип токена (всегда Bearer)",
            example = "Bearer",
            defaultValue = "Bearer"
    )
    private String tokenType = "Bearer";

    @Schema(
            description = "Уникальный идентификатор пользователя в системе",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long userId;

    @Schema(
            description = "Email аутентифицированного пользователя",
            example = "client@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @Schema(
            description = "Роль пользователя в системе",
            example = "CLIENT",
            allowableValues = {"CLIENT", "MASTER", "ADMIN"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String role;

    @Schema(
            description = "Время жизни Access Token в миллисекундах",
            example = "86400000",
            defaultValue = "86400000"
    )
    private Long expiresIn = 86400000L; // 24 часа

    public JwtResponse(String accessToken, String refreshToken, Long userId, String email, String role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.email = email;
        this.role = role;
    }
}