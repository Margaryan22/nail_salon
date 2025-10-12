package Frolov_back.NAILS_WEB_APP.controller;

import Frolov_back.NAILS_WEB_APP.service.impl.RegistrationServiceFactory;
import Frolov_back.NAILS_WEB_APP.service.UserValidationService;
import Frolov_back.NAILS_WEB_APP.service.DTO.UserRegistrationRequestDto;
import Frolov_back.NAILS_WEB_APP.service.DTO.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication & Registration", description = "API для регистрации и аутентификации пользователей")
public class RegistrationController {

    private final RegistrationServiceFactory registrationServiceFactory;
    private final UserValidationService validationService;

    public RegistrationController(RegistrationServiceFactory registrationServiceFactory,
                                  UserValidationService validationService) {
        this.registrationServiceFactory = registrationServiceFactory;
        this.validationService = validationService;
    }

    /**
     * Универсальный endpoint для регистрации пользователей любого типа
     */
    @Operation(
            summary = "Регистрация пользователя",
            description = "Регистрирует нового пользователя указанного типа (ADMIN, CLIENT, MASTER)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных"),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для регистрации",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserRegistrationRequestDto.class))
            )
            @RequestBody UserRegistrationRequestDto requestDto
    ) {
        // Валидация запроса
        UserValidationService.ValidationResult validation = validationService.validateRegistration(requestDto);
        if (!validation.isValid()) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", "Ошибка валидации",
                            "errors", validation.getErrors()
                    ));
        }

        try {
            // Выбор нужного сервиса регистрации
            var registrationService = registrationServiceFactory.getService(requestDto.getRole());

            // Регистрация пользователя
            UserResponseDto registeredUser = registrationService.register(requestDto);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "success", true,
                            "message", "Пользователь успешно зарегистрирован",
                            "data", registeredUser
                    ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Внутренняя ошибка сервера при регистрации"
                    ));
        }
    }

    /**
     * Получить список поддерживаемых ролей для регистрации
     */
    @Operation(summary = "Получить поддерживаемые роли")
    @GetMapping("/supported-roles")
    public ResponseEntity<?> getSupportedRoles() {
        var supportedRoles = registrationServiceFactory.getSupportedRoles();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "supportedRoles", supportedRoles
        ));
    }

    /**
     * Проверить доступность email
     */
    @Operation(summary = "Проверить доступность email")
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailAvailability(@RequestParam String email) {
        boolean isAvailable = !validationService.isEmailTaken(email);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "email", email,
                "available", isAvailable
        ));
    }

    /**
     * Проверить доступность телефона
     */
    @Operation(summary = "Проверить доступность телефона")
    @GetMapping("/check-phone")
    public ResponseEntity<?> checkPhoneAvailability(@RequestParam String phone) {
        boolean isAvailable = !validationService.isPhoneTaken(phone);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "phone", phone,
                "available", isAvailable
        ));
    }
}