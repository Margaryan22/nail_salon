package Frolov_back.NAILS_WEB_APP.service;

import Frolov_back.NAILS_WEB_APP.domain.UserRoleType;
import Frolov_back.NAILS_WEB_APP.DTO.UserRegistrationRequestDto;
import Frolov_back.NAILS_WEB_APP.DTO.UserResponseDto;

public interface RegistrationService {

    /**
     * Регистрирует пользователя указанного типа
     */
    UserResponseDto register(UserRegistrationRequestDto requestDto);

    /**
     * Проверяет, поддерживает ли сервис указанный тип пользователя
     */
    boolean supports(UserRoleType roleType);

    /**
     * Валидирует данные для регистрации
     */
    boolean validateRegistrationData(UserRegistrationRequestDto requestDto);
}