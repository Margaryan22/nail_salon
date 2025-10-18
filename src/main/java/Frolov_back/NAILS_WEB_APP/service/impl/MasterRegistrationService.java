package Frolov_back.NAILS_WEB_APP.service.impl;

import Frolov_back.NAILS_WEB_APP.domain.MasterProfile;
import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import Frolov_back.NAILS_WEB_APP.domain.UserRoleType;
import Frolov_back.NAILS_WEB_APP.repository.MasterProfileRepository;
import Frolov_back.NAILS_WEB_APP.repository.SystemUserRepository;
import Frolov_back.NAILS_WEB_APP.service.BaseRegistrationService;
import Frolov_back.NAILS_WEB_APP.service.DTO.UserRegistrationRequestDto;
import Frolov_back.NAILS_WEB_APP.service.DTO.UserResponseDto;
import Frolov_back.NAILS_WEB_APP.service.RegistrationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MasterRegistrationService extends BaseRegistrationService implements RegistrationService {

    private final SystemUserRepository systemUserRepository;
    private final MasterProfileRepository masterProfileRepository;

    public MasterRegistrationService(SystemUserRepository systemUserRepository,
                                     MasterProfileRepository masterProfileRepository,
                                     PasswordEncoder passwordEncoder) {
        super(passwordEncoder);
        this.systemUserRepository = systemUserRepository;
        this.masterProfileRepository = masterProfileRepository;
    }

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        // Создаем базового пользователя
        SystemUser newUser = createBasicUser(requestDto);
        newUser.setRole(UserRoleType.MASTER);

        SystemUser savedUser = systemUserRepository.save(newUser);

        // Создаем профиль мастера
        MasterProfile masterProfile = new MasterProfile(savedUser);
        masterProfile.setSpecialization(requestDto.getSpecialization());
        masterProfile.setWorkExperience(requestDto.getWorkExperience());
        masterProfile.setDescription(requestDto.getDescription());
        masterProfile.setPhotoUrl(requestDto.getPhotoUrl());
        masterProfile.setIsActive(true); // Новый мастер активен по умолчанию

        masterProfileRepository.save(masterProfile);
        savedUser.setMasterProfile(masterProfile);

        return convertToDto(savedUser);
    }

    @Override
    public boolean supports(UserRoleType roleType) {
        return UserRoleType.MASTER == roleType;
    }

    @Override
    public boolean validateRegistrationData(UserRegistrationRequestDto requestDto) {
        // Базовая валидация
        if (requestDto.getEmail() == null || requestDto.getEmail().trim().isEmpty() ||
                requestDto.getPassword() == null || requestDto.getPassword().trim().isEmpty() ||
                requestDto.getFirstName() == null || requestDto.getFirstName().trim().isEmpty() ||
                requestDto.getLastName() == null || requestDto.getLastName().trim().isEmpty()) {
            return false;
        }

        // Дополнительная валидация для мастера
        if (requestDto.getSpecialization() == null || requestDto.getSpecialization().trim().isEmpty()) {
            return false; // Специализация обязательна
        }

        if (requestDto.getWorkExperience() != null && requestDto.getWorkExperience() < 0) {
            return false; // Опыт работы не может быть отрицательным
        }

        return true;
    }

    private UserResponseDto convertToDto(SystemUser user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setRole(user.getRole().name());
        dto.setUserType("MASTER");
        return dto;
    }
}