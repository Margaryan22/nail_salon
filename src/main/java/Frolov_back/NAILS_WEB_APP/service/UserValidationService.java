package Frolov_back.NAILS_WEB_APP.service;

import Frolov_back.NAILS_WEB_APP.repository.SystemUserRepository;
import Frolov_back.NAILS_WEB_APP.service.impl.RegistrationServiceFactory;
import Frolov_back.NAILS_WEB_APP.service.DTO.UserRegistrationRequestDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserValidationService {

    private final SystemUserRepository systemUserRepository;
    private final RegistrationServiceFactory registrationServiceFactory;

    public UserValidationService(SystemUserRepository systemUserRepository,
                                 RegistrationServiceFactory registrationServiceFactory) {
        this.systemUserRepository = systemUserRepository;
        this.registrationServiceFactory = registrationServiceFactory;
    }

    /**
     * Валидирует запрос на регистрацию и возвращает результат с ошибками
     */
    public ValidationResult validateRegistration(UserRegistrationRequestDto requestDto) {
        List<String> errors = new ArrayList<>();

        // Базовая валидация обязательных полей
        if (!validatePassword(requestDto.getPassword())) {
            errors.add("Пароль должен содержать минимум 6 символов");
        }

        if (requestDto.getEmail() == null || requestDto.getEmail().trim().isEmpty()) {
            errors.add("Email обязателен для заполнения");
        }

        if (requestDto.getPassword() == null || requestDto.getPassword().trim().isEmpty()) {
            errors.add("Пароль обязателен для заполнения");
        }

        if (requestDto.getFirstName() == null || requestDto.getFirstName().trim().isEmpty()) {
            errors.add("Имя обязательно для заполнения");
        }

        if (requestDto.getLastName() == null || requestDto.getLastName().trim().isEmpty()) {
            errors.add("Фамилия обязательна для заполнения");
        }

        if (requestDto.getRole() == null) {
            errors.add("Роль пользователя обязательна для заполнения");
        }

        // Проверка email на уникальность
        if (requestDto.getEmail() != null && systemUserRepository.existsByEmail(requestDto.getEmail())) {
            errors.add("Email уже занят");
        }

        // Проверка телефона на уникальность (если указан)
        if (requestDto.getPhone() != null && !requestDto.getPhone().trim().isEmpty() &&
                systemUserRepository.existsByPhone(requestDto.getPhone())) {
            errors.add("Телефон уже занят");
        }

        // Проверка поддержки роли
        if (requestDto.getRole() != null && !registrationServiceFactory.supports(requestDto.getRole())) {
            errors.add("Указанная роль не поддерживается: " + requestDto.getRole());
        }

        // Специфичная валидация через соответствующий сервис регистрации
        if (requestDto.getRole() != null && registrationServiceFactory.supports(requestDto.getRole())) {
            RegistrationService registrationService = registrationServiceFactory.getService(requestDto.getRole());
            if (!registrationService.validateRegistrationData(requestDto)) {
                errors.add("Данные не прошли валидацию для роли: " + requestDto.getRole());
            }
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Валидация пароля
     */
    public boolean validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        // Минимальные требования к паролю
        if (password.length() < 6) {
            return false;
        }

        // Можно добавить дополнительные проверки:
        // - наличие цифр
        // - наличие букв в разных регистрах
        // - специальные символы

        return true;
    }

    /**
     * Проверяет занят ли email
     */
    public boolean isEmailTaken(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return systemUserRepository.existsByEmail(email.trim().toLowerCase());
    }

    /**
     * Проверяет занят ли телефон
     */
    public boolean isPhoneTaken(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return systemUserRepository.existsByPhone(phone.trim());
    }

    /**
     * Результат валидации с ошибками
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public boolean isValid() { return valid; }
        public List<String> getErrors() { return errors; }

    }

}