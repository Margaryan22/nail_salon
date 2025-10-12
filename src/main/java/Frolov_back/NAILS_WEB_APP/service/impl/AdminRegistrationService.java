package Frolov_back.NAILS_WEB_APP.service.impl;

import Frolov_back.NAILS_WEB_APP.domain.AdminProfile;
import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import Frolov_back.NAILS_WEB_APP.domain.UserRoleType;
import Frolov_back.NAILS_WEB_APP.repository.AdminProfileRepository;
import Frolov_back.NAILS_WEB_APP.repository.SystemUserRepository;
import Frolov_back.NAILS_WEB_APP.service.DTO.UserRegistrationRequestDto;
import Frolov_back.NAILS_WEB_APP.service.DTO.UserResponseDto;
import Frolov_back.NAILS_WEB_APP.service.RegistrationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AdminRegistrationService implements RegistrationService {

    private final SystemUserRepository systemUserRepository;
    private final AdminProfileRepository adminProfileRepository;

    public AdminRegistrationService(SystemUserRepository systemUserRepository,
                                    AdminProfileRepository adminProfileRepository) {
        this.systemUserRepository = systemUserRepository;
        this.adminProfileRepository = adminProfileRepository;
    }

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        // Создаем базового пользователя
        SystemUser newUser = createBasicUser(requestDto);
        newUser.setRole(UserRoleType.ADMIN);

        SystemUser savedUser = systemUserRepository.save(newUser);

        // Создаем профиль администратора
        Integer permissionsLevel = requestDto.getPermissionsLevel() != null ?
                requestDto.getPermissionsLevel() : 1; // По умолчанию обычный админ

        AdminProfile adminProfile = new AdminProfile(savedUser, permissionsLevel);
        adminProfileRepository.save(adminProfile);
        savedUser.setAdminProfile(adminProfile);

        return convertToDto(savedUser);
    }

    @Override
    public boolean supports(UserRoleType roleType) {
        return UserRoleType.ADMIN == roleType;
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

        // Дополнительная валидация для админа
        if (requestDto.getPermissionsLevel() != null &&
                (requestDto.getPermissionsLevel() < 1 || requestDto.getPermissionsLevel() > 2)) {
            return false;
        }

        return true;
    }

    private SystemUser createBasicUser(UserRegistrationRequestDto requestDto) {
        SystemUser user = new SystemUser();
        user.setEmail(requestDto.getEmail().trim().toLowerCase());
        user.setPasswordHash(requestDto.getPassword()); // TODO: добавить хеширование
        user.setFirstName(requestDto.getFirstName().trim());
        user.setLastName(requestDto.getLastName().trim());
        user.setPhone(requestDto.getPhone() != null ? requestDto.getPhone().trim() : null);
        user.setCreatedAt(LocalDateTime.now());
        return user;
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
        dto.setUserType("ADMIN");
        return dto;
    }
}