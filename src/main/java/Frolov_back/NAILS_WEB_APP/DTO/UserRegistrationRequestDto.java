package Frolov_back.NAILS_WEB_APP.DTO;

import java.time.LocalDate;

// --- DTO для запроса регистрации ---
import Frolov_back.NAILS_WEB_APP.domain.UserRoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Запрос на регистрацию пользователя")
@Data
public class UserRegistrationRequestDto {
    @Schema(description = "Email пользователя", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    @Schema(description = "Пароль", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
    @Schema(description = "Имя", example = "Иван", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;
    @Schema(description = "Фамилия", example = "Иванов", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;
    @Schema(description = "Телефон", example = "+79991234567")
    private String phone;
    @Schema(description = "Роль пользователя", example = "CLIENT", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserRoleType role; // Теперь роль указывается явно

    // Дополнительные поля для разных ролей
    @Schema(description = "Дата рождения (для клиента)", example = "1990-05-15")
    private LocalDate birthdate; // Для клиента
    @Schema(description = "Специализация (для мастера)", example = "Ногтевой сервис")
    private String specialization; // Для мастера
    @Schema(description = "Опыт работы в годах (для мастера)", example = "3")
    private Integer workExperience; // Для мастера
    @Schema(description = "Примечание (для мастера)", example = "ну типа примечание ебать)")
    private String description; // Для мастера
    @Schema(description = "URL фотки (мб понадобится потом) (для мастера)", example = "3")
    private String photoUrl; // Для мастера
    @Schema(description = "Уровень прав (для администратора)", example = "1")
    private Integer permissionsLevel; // Для админа
}