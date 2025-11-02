package Frolov_back.NAILS_WEB_APP.service.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Запрос на обновление Access Token с помощью Refresh Token")
public class RefreshTokenRequest {

    @Schema(
            description = "Refresh Token полученный при предыдущей аутентификации",
            example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String refreshToken;
}
