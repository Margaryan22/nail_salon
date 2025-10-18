package Frolov_back.NAILS_WEB_APP.controller;

import Frolov_back.NAILS_WEB_APP.service.impl.AuthenticationService;
import Frolov_back.NAILS_WEB_APP.service.DTO.JwtResponse;
import Frolov_back.NAILS_WEB_APP.service.DTO.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API для аутентификации")
public class AuthController {

    private final AuthenticationService authenticationService;

    @Operation(
            summary = "🔐 Аутентификация пользователя",
            description = """
            ### Вход в систему для получения JWT токена
            - Проверяет email и пароль
            - Возвращает access и refresh токены
            - Токен действителен 24 часа
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Успешная аутентификация",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "❌ Неверные учетные данные",
                    content = @Content(examples = @ExampleObject(value = "{\"error\": \"Invalid credentials\"}"))
            )
    })
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для входа",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    name = "Пример запроса",
                                    value = """
                            {
                                "email": "client@example.com",
                                "password": "password123"
                            }
                            """
                            )
                    )
            )
            @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authenticationService.authenticate(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @Operation(
            summary = "🔄 Обновление access токена",
            description = "Используйте refresh token для получения нового access token"
    )
    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        JwtResponse jwtResponse = authenticationService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(jwtResponse);
    }

    @Operation(summary = "Выход из системы")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request) {
        // В будущем можно добавить логику отзыва токенов
        return ResponseEntity.ok(Map.of("message", "Успешный выход из системы"));
    }
}

// Добавляем DTO для refresh token запроса
class RefreshTokenRequest {
    private String refreshToken;

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}