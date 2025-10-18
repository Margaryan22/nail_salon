package Frolov_back.NAILS_WEB_APP.service;

import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import Frolov_back.NAILS_WEB_APP.service.DTO.UserRegistrationRequestDto;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

public abstract class BaseRegistrationService {

    protected final PasswordEncoder passwordEncoder;

    protected BaseRegistrationService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Создает базового пользователя с хешированным паролем
     */
    protected SystemUser createBasicUser(UserRegistrationRequestDto requestDto) {
        SystemUser user = new SystemUser();
        user.setEmail(requestDto.getEmail().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(requestDto.getPassword())); // ХЕШИРОВАНИЕ!
        user.setFirstName(requestDto.getFirstName().trim());
        user.setLastName(requestDto.getLastName().trim());
        user.setPhone(requestDto.getPhone() != null ? requestDto.getPhone().trim() : null);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }
}
