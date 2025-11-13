package Frolov_back.NAILS_WEB_APP.impl;

import Frolov_back.NAILS_WEB_APP.domain.ClientProfile;
import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import Frolov_back.NAILS_WEB_APP.domain.UserRoleType;
import Frolov_back.NAILS_WEB_APP.repository.ClientProfileRepository;
import Frolov_back.NAILS_WEB_APP.repository.SystemUserRepository;
import Frolov_back.NAILS_WEB_APP.service.BaseRegistrationService;
import Frolov_back.NAILS_WEB_APP.DTO.UserRegistrationRequestDto;
import Frolov_back.NAILS_WEB_APP.DTO.UserResponseDto;
import Frolov_back.NAILS_WEB_APP.service.RegistrationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ClientRegistrationService extends BaseRegistrationService implements RegistrationService {

    private final SystemUserRepository systemUserRepository;
    private final ClientProfileRepository clientProfileRepository;

    public ClientRegistrationService(SystemUserRepository systemUserRepository,
                                     ClientProfileRepository clientProfileRepository,
                                     PasswordEncoder passwordEncoder) {
        super(passwordEncoder);
        this.systemUserRepository = systemUserRepository;
        this.clientProfileRepository = clientProfileRepository;
    }

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        // Создаем базового пользователя
        SystemUser newUser = createBasicUser(requestDto);
        newUser.setRole(UserRoleType.CLIENT);

        SystemUser savedUser = systemUserRepository.save(newUser);

        // Создаем профиль клиента
        ClientProfile clientProfile = new ClientProfile(savedUser);
        clientProfile.setBirthdate(requestDto.getBirthdate());
        clientProfile.setBonusPoints(0); // Новый клиент без бонусов
        clientProfileRepository.save(clientProfile);
        savedUser.setClientProfile(clientProfile);

        return convertToDto(savedUser);
    }

    @Override
    public boolean supports(UserRoleType roleType) {
        return UserRoleType.CLIENT == roleType;
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

        // Дополнительная валидация для клиента
        if (requestDto.getBirthdate() != null && requestDto.getBirthdate().isAfter(LocalDateTime.now().toLocalDate())) {
            return false; // Дата рождения не может быть в будущем
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
        dto.setUserType("CLIENT");
        return dto;
    }
}