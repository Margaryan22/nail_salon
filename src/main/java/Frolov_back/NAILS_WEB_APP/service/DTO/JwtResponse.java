package Frolov_back.NAILS_WEB_APP.service.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Ответ с JWT токенами")
public class JwtResponse {
    @Schema(description = "Access token для доступа к API", example = "eyJhbGciOiJIUzI1NiIs...")
    private String accessToken;

    @Schema(description = "Refresh token для обновления access token", example = "a1b2c3d4-e5f6-...")
    private String refreshToken;

    @Schema(description = "Тип токена", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "ID пользователя", example = "1")
    private Long userId;

    @Schema(description = "Email пользователя", example = "client@example.com")
    private String email;

    @Schema(description = "Роль пользователя", example = "CLIENT")
    private String role;

    public JwtResponse(String accessToken, String refreshToken, Long userId, String email, String role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    // Геттеры
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getTokenType() { return tokenType; }
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}