package Frolov_back.NAILS_WEB_APP.service.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

// --- DTO для запроса регистрации ---
import Frolov_back.NAILS_WEB_APP.domain.UserRoleType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
@Schema(description = "Запрос на регистрацию пользователя")
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

    // Геттеры и сеттеры
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public UserRoleType getRole() { return role; }
    public void setRole(UserRoleType role) { this.role = role; }

    public LocalDate getBirthdate() { return birthdate; }
    public void setBirthdate(LocalDate birthdate) { this.birthdate = birthdate; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public Integer getWorkExperience() { return workExperience; }
    public void setWorkExperience(Integer workExperience) { this.workExperience = workExperience; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public Integer getPermissionsLevel() { return permissionsLevel; }
    public void setPermissionsLevel(Integer permissionsLevel) { this.permissionsLevel = permissionsLevel; }
}