// TODO 04.10.2025: проработать полную цепочку регистрации пользователя (как админа) и вернуть объект зарегистрированного пользователя (JSON)
// TODO 08.10.2025: Кривая реализация готова -> далее нужно подойти более правильно



package Frolov_back.NAILS_WEB_APP.controller;


import Frolov_back.NAILS_WEB_APP.service.UserService;
import Frolov_back.NAILS_WEB_APP.service.DTO.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "2. Управление пользователями", description = "API для работы с пользователями (требует аутентификации)")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // === ТОЛЬКО endpoints для управления существующими пользователями ===

    // ПОЛУЧЕНИЕ ПОЛЬЗОВАТЕЛЕЙ
    @Operation(
            summary = "👤 Получить информацию о текущем пользователе",
            description = "Возвращает данные пользователя по JWT токену",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "✅ Данные пользователя"),
            @ApiResponse(responseCode = "401", description = "❌ Пользователь не аутентифицирован")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Пользователь не аутентифицирован");
        }

        String email = authentication.getName();
        return userService.getCurrentUser(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "📋 Получить всех пользователей",
            description = "Возвращает список всех пользователей системы",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Получить пользователя по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{userId}/detailed")
    public ResponseEntity<?> getUserWithProfile(@PathVariable Long userId) {
        return userService.getUserWithProfile(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponseDto>> getUsersByRole(@PathVariable String role) {
        List<UserResponseDto> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "🔍 Поиск пользователей",
            description = "Поиск и фильтрация пользователей по различным критериям",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDto>> searchUsers(
            @Parameter(description = "Email для поиска") @RequestParam(required = false)  String email,
            @Parameter(description = "Имя для поиска") @RequestParam(required = false) String firstName,
            @Parameter(description = "Фамилия для поиска") @RequestParam(required = false) String lastName,
            @Parameter(description = "Телефон для поиска") @RequestParam(required = false) String phone,
            @Parameter(description = "Роль для фильтрации") @RequestParam(required = false) String role) {

        UserSearchCriteriaDto criteria = new UserSearchCriteriaDto();
        criteria.setEmail(email);
        criteria.setFirstName(firstName);
        criteria.setLastName(lastName);
        criteria.setPhone(phone);
        criteria.setRole(role);

        List<UserResponseDto> users = userService.searchUsers(criteria);
        return ResponseEntity.ok(users);
    }

    // ОБНОВЛЕНИЕ
    @Operation(
            summary = "🔄 Обновить данные пользователя",
            description = "Обновление основной информации пользователя",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Long userId,
            @RequestBody UserUpdateRequestDto requestDto) {
        // Простая валидация для обновления
        if (requestDto.getFirstName() == null || requestDto.getFirstName().trim().isEmpty() ||
                requestDto.getLastName() == null || requestDto.getLastName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Имя и фамилия обязательны для заполнения");
        }

        return userService.updateUser(userId, requestDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}/client-profile")
    public ResponseEntity<?> updateClientProfile(@PathVariable Long userId,
                                                 @RequestBody ClientProfileDto profileDto) {
        return userService.updateClientProfile(userId, profileDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}/master-profile")
    public ResponseEntity<?> updateMasterProfile(@PathVariable Long userId,
                                                 @RequestBody MasterProfileDto profileDto) {
        return userService.updateMasterProfile(userId, profileDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // АДМИНСКИЕ ФУНКЦИИ
    @GetMapping("/admins")
    public ResponseEntity<List<UserResponseDto>> getAllAdmins() {
        List<UserResponseDto> admins = userService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }

    @PostMapping("/{userId}/promote-to-super-admin")
    public ResponseEntity<?> promoteToSuperAdmin(@PathVariable Long userId) {
        return userService.promoteToSuperAdmin(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/demote-to-regular-admin")
    public ResponseEntity<?> demoteToRegularAdmin(@PathVariable Long userId) {
        return userService.demoteToRegularAdmin(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{userId}/is-super-admin")
    public ResponseEntity<Boolean> isSuperAdmin(@PathVariable Long userId) {
        boolean isSuperAdmin = userService.isSuperAdmin(userId);
        return ResponseEntity.ok(isSuperAdmin);
    }

    // УПРАВЛЕНИЕ АКТИВНОСТЬЮ
    @PostMapping("/{userId}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long userId) {
        boolean success = userService.deactivateUser(userId);
        if (success) {
            return ResponseEntity.ok("Пользователь деактивирован");
        } else {
            return ResponseEntity.badRequest().body("Не удалось деактивировать пользователя");
        }
    }

    @PostMapping("/{userId}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long userId) {
        boolean success = userService.activateUser(userId);
        if (success) {
            return ResponseEntity.ok("Пользователь активирован");
        } else {
            return ResponseEntity.badRequest().body("Не удалось активировать пользователя");
        }
    }
}