package Frolov_back.NAILS_WEB_APP.controller;

import Frolov_back.NAILS_WEB_APP.security.AuthenticationService;
import Frolov_back.NAILS_WEB_APP.DTO.JwtResponse;
import Frolov_back.NAILS_WEB_APP.DTO.LoginRequest;
import Frolov_back.NAILS_WEB_APP.DTO.RefreshTokenRequest;
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
@Tag(
        name = "1. 🔐 Аутентификация",
        description = """
        ## API для аутентификации и управления токенами
        
        ### 📋 Общий процесс аутентификации:
        1. **Логин** → получаете Access Token и Refresh Token
        2. **Используете Access Token** в заголовке `Authorization: Bearer {token}`
        3. **При истечении Access Token** → используете Refresh Token для получения нового
        4. **При истечении Refresh Token** → требуется повторный логин
        
        ### ⏱️ Время жизни токенов:
        - **Access Token**: 24 часа
        - **Refresh Token**: 7 дней
        
        ### 🔒 Защищенные endpoint'ы:
        - Все endpoint'ы кроме `/api/v1/auth/**` требуют валидный Access Token
        """
)
public class AuthController {

    private final AuthenticationService authenticationService;

    @Operation(
            summary = "🔑 Аутентификация пользователя",
            description = """
            ### Процесс аутентификации:
            1. Проверяются учетные данные (email и пароль)
            2. Генерируется JWT Access Token (24 часа)
            3. Генерируется Refresh Token (7 дней)
            4. Возвращаются оба токена для использования
            
            ### Использование токенов:
            - **Access Token**: Добавлять в заголовок `Authorization: Bearer {accessToken}`
            - **Refresh Token**: Сохранить для обновления Access Token
            
            ### Пример заголовка для защищенных запросов:
            ```
            Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
            ```
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = """
                ✅ Успешная аутентификация
                - Возвращены Access Token и Refresh Token
                - Пользователь может обращаться к защищенным ресурсам
                """,
                    content = @Content(
                            schema = @Schema(implementation = JwtResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    {
                        "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                        "refreshToken": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                        "tokenType": "Bearer",
                        "userId": 1,
                        "email": "client@example.com",
                        "role": "CLIENT",
                        "expiresIn": 86400000
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = """
                ❌ Ошибка аутентификации
                - Неверный email или пароль
                - Пользователь не найден
                - Учетная запись заблокирована
                """,
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                    {
                        "timestamp": "2023-10-01T12:00:00.000+00:00",
                        "status": 401,
                        "error": "Unauthorized",
                        "message": "Invalid credentials",
                        "path": "/api/v1/auth/login"
                    }
                    """
                            )
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Учетные данные пользователя для входа в систему",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Вход клиента",
                                            value = """
                                {
                                    "email": "client@example.com",
                                    "password": "password123"
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Вход мастера",
                                            value = """
                                {
                                    "email": "master@salon.com", 
                                    "password": "master123"
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Вход администратора",
                                            value = """
                                {
                                    "email": "admin@salon.com",
                                    "password": "admin123"
                                }
                                """
                                    )
                            }
                    )
            )
            @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authenticationService.authenticate(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @Operation(
            summary = "🔄 Обновление Access Token",
            description = """
            ### Когда использовать:
            - Access Token истек (получаете 401 ошибку)
            - Нужно продолжить работу без повторного входа
            
            ### Процесс обновления:
            1. Отправляете Refresh Token в запросе
            2. Система проверяет валидность Refresh Token
            3. Генерируется новая пара токенов
            4. Старый Refresh Token становится невалидным
            
            ### Важно:
            - Refresh Token можно использовать только один раз
            - После обновления получаете новую пару токенов
            - Старые токены становятся недействительными
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Токены успешно обновлены",
                    content = @Content(
                            schema = @Schema(implementation = JwtResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    {
                        "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                        "refreshToken": "new-refresh-token-12345",
                        "tokenType": "Bearer",
                        "userId": 1,
                        "email": "client@example.com",
                        "role": "CLIENT",
                        "expiresIn": 86400000
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "❌ Неверный или просроченный Refresh Token",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                    {
                        "timestamp": "2023-10-01T12:00:00.000+00:00",
                        "status": 400,
                        "error": "Bad Request",
                        "message": "Refresh token был просрочен. Пожалуйста, войдите снова.",
                        "path": "/api/v1/auth/refresh-token"
                    }
                    """
                            )
                    )
            )
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Refresh Token для получения новой пары токенов",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RefreshTokenRequest.class),
                            examples = @ExampleObject(
                                    value = """
                        {
                            "refreshToken": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
                        }
                        """
                            )
                    )
            )
            @RequestBody RefreshTokenRequest request) {
        JwtResponse jwtResponse = authenticationService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(jwtResponse);
    }

    @Operation(
            summary = "🚪 Выход из системы",
            description = """
            ### Что происходит при выходе:
            - Refresh Token пользователя удаляется из системы
            - Токены становятся недействительными
            - Для следующего входа требуется повторная аутентификация
            
            ### Важно:
            - Access Token остается валидным до истечения срока
            - Рекомендуется удалить токены на клиенте после выхода
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Успешный выход из системы",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                    {
                        "message": "Успешный выход из системы"
                    }
                    """
                            )
                    )
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Refresh Token для инвалидации",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RefreshTokenRequest.class),
                            examples = @ExampleObject(
                                    value = """
                        {
                            "refreshToken": "a1b2c3d4-e5f6-7890-abcd-ef1234567890" 
                        }
                        """
                            )
                    )
            )
            @RequestBody RefreshTokenRequest request) {
        // В будущем можно добавить логику отзыва токенов
        return ResponseEntity.ok(Map.of("message", "Успешный выход из системы"));
    }
}
