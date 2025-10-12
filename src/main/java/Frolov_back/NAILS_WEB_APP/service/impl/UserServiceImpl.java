package Frolov_back.NAILS_WEB_APP.service.impl;

// УДАЛЯЕМ импорты связанные с регистрацией
// import Frolov_back.NAILS_WEB_APP.service.DTO.UserRegistrationRequestDto;
// import Frolov_back.NAILS_WEB_APP.service.DTO.ClientRegistrationRequestDto;
// import Frolov_back.NAILS_WEB_APP.service.DTO.MasterRegistrationRequestDto;

// ОСТАВЛЯЕМ только нужные импорты
import Frolov_back.NAILS_WEB_APP.domain.*;
import Frolov_back.NAILS_WEB_APP.repository.*;
import Frolov_back.NAILS_WEB_APP.service.DTO.*;
import Frolov_back.NAILS_WEB_APP.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final SystemUserRepository systemUserRepository;
    private final AdminProfileRepository adminProfileRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final MasterProfileRepository masterProfileRepository;

    public UserServiceImpl(SystemUserRepository systemUserRepository,
                           AdminProfileRepository adminProfileRepository,
                           ClientProfileRepository clientProfileRepository,
                           MasterProfileRepository masterProfileRepository) {
        this.systemUserRepository = systemUserRepository;
        this.adminProfileRepository = adminProfileRepository;
        this.clientProfileRepository = clientProfileRepository;
        this.masterProfileRepository = masterProfileRepository;
    }

    // === УДАЛЕНО: все методы регистрации ===

    // === ПОИСК И ПОЛУЧЕНИЕ (БЕЗ ИЗМЕНЕНИЙ) ===
    @Override
    public Optional<SystemUser> findUserByEmail(String email) {
        return systemUserRepository.findByEmail(email);
    }

    @Override
    public Optional<UserResponseDto> getUserById(Long userId) {
        return systemUserRepository.findById(userId)
                .map(this::convertToDto);
    }

    @Override
    public Optional<UserWithProfileDto> getUserWithProfile(Long userId) {
        return systemUserRepository.findById(userId)
                .map(this::convertToDetailedDto);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return systemUserRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDto> getUsersByRole(String role) {
        try {
            UserRoleType roleType = UserRoleType.valueOf(role.toUpperCase());
            return systemUserRepository.findByRole(roleType).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    @Override
    public List<UserResponseDto> searchUsers(UserSearchCriteriaDto criteria) {
        // Реализация без изменений
        List<SystemUser> users = systemUserRepository.findAll();
        return users.stream()
                .filter(user -> criteria.getEmail() == null ||
                        user.getEmail().toLowerCase().contains(criteria.getEmail().toLowerCase()))
                .filter(user -> criteria.getFirstName() == null ||
                        user.getFirstName().toLowerCase().contains(criteria.getFirstName().toLowerCase()))
                .filter(user -> criteria.getLastName() == null ||
                        user.getLastName().toLowerCase().contains(criteria.getLastName().toLowerCase()))
                .filter(user -> criteria.getPhone() == null ||
                        (user.getPhone() != null && user.getPhone().contains(criteria.getPhone())))
                .filter(user -> criteria.getRole() == null ||
                        user.getRole().name().equalsIgnoreCase(criteria.getRole()))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // === ОБНОВЛЕНИЕ (БЕЗ ИЗМЕНЕНИЙ) ===
    @Override
    @Transactional
    public Optional<UserResponseDto> updateUser(Long userId, UserUpdateRequestDto requestDto) {
        return systemUserRepository.findById(userId)
                .map(user -> {
                    user.setFirstName(requestDto.getFirstName());
                    user.setLastName(requestDto.getLastName());
                    user.setPhone(requestDto.getPhone());
                    return systemUserRepository.save(user);
                })
                .map(this::convertToDto);
    }

    @Override
    @Transactional
    public Optional<UserResponseDto> updateClientProfile(Long userId, ClientProfileDto profileDto) {
        return clientProfileRepository.findByUserId(userId)
                .map(profile -> {
                    profile.setBirthdate(profileDto.getBirthdate());
                    profile.setBonusPoints(profileDto.getBonusPoints());
                    profile.setNotes(profileDto.getNotes());
                    return clientProfileRepository.save(profile);
                })
                .map(ClientProfile::getSystemUser)
                .map(this::convertToDto);
    }

    @Override
    @Transactional
    public Optional<UserResponseDto> updateMasterProfile(Long userId, MasterProfileDto profileDto) {
        return masterProfileRepository.findByUserId(userId)
                .map(profile -> {
                    profile.setSpecialization(profileDto.getSpecialization());
                    profile.setWorkExperience(profileDto.getWorkExperience());
                    profile.setDescription(profileDto.getDescription());
                    profile.setPhotoUrl(profileDto.getPhotoUrl());
                    profile.setIsActive(profileDto.getIsActive());
                    return masterProfileRepository.save(profile);
                })
                .map(MasterProfile::getSystemUser)
                .map(this::convertToDto);
    }

    // === АДМИНСКИЕ ФУНКЦИИ (БЕЗ ИЗМЕНЕНИЙ) ===
    @Override
    public List<UserResponseDto> getAllAdmins() {
        return systemUserRepository.findByRole(UserRoleType.ADMIN).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<UserResponseDto> promoteToSuperAdmin(Long userId) {
        return adminProfileRepository.findBySystemUserUserId(userId)
                .map(profile -> {
                    profile.setPermissionsLevel(2);
                    return adminProfileRepository.save(profile);
                })
                .map(AdminProfile::getSystemUser)
                .map(this::convertToDto);
    }

    @Override
    @Transactional
    public Optional<UserResponseDto> demoteToRegularAdmin(Long userId) {
        return adminProfileRepository.findBySystemUserUserId(userId)
                .map(profile -> {
                    profile.setPermissionsLevel(1);
                    return adminProfileRepository.save(profile);
                })
                .map(AdminProfile::getSystemUser)
                .map(this::convertToDto);
    }

    @Override
    public boolean isSuperAdmin(Long userId) {
        return adminProfileRepository.isSuperAdmin(userId);
    }

    // === УПРАВЛЕНИЕ АКТИВНОСТЬЮ (БЕЗ ИЗМЕНЕНИЙ) ===
    @Override
    @Transactional
    public boolean deactivateUser(Long userId) {
        return masterProfileRepository.findByUserId(userId)
                .map(profile -> {
                    profile.setIsActive(false);
                    masterProfileRepository.save(profile);
                    return true;
                })
                .orElseGet(() -> {
                    return false;
                });
    }

    @Override
    @Transactional
    public boolean activateUser(Long userId) {
        return masterProfileRepository.findByUserId(userId)
                .map(profile -> {
                    profile.setIsActive(true);
                    masterProfileRepository.save(profile);
                    return true;
                })
                .orElse(false);
    }

    // === ВАЛИДАЦИЯ (БЕЗ ИЗМЕНЕНИЙ) ===
    @Override
    public boolean isEmailAvailable(String email) {
        return !systemUserRepository.existsByEmail(email);
    }

    @Override
    public boolean isPhoneAvailable(String phone) {
        if (phone == null || phone.trim().isEmpty()) return true;
        return !systemUserRepository.existsByPhone(phone);
    }

    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ (БЕЗ ИЗМЕНЕНИЙ) ===
    private UserResponseDto convertToDto(SystemUser user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setRole(user.getRole().name());
        dto.setUserType(determineUserType(user));
        return dto;
    }

    private UserWithProfileDto convertToDetailedDto(SystemUser user) {
        UserWithProfileDto dto = new UserWithProfileDto();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setRole(user.getRole().name());

        if (user.getAdminProfile() != null) {
            AdminProfileDto adminDto = new AdminProfileDto();
            adminDto.setAdminId(user.getAdminProfile().getAdminId());
            adminDto.setUserId(user.getUserId());
            adminDto.setPermissionsLevel(user.getAdminProfile().getPermissionsLevel());
            dto.setAdminProfile(adminDto);
        }

        if (user.getClientProfile() != null) {
            ClientProfileDto clientDto = new ClientProfileDto();
            clientDto.setUserId(user.getUserId());
            clientDto.setBirthdate(user.getClientProfile().getBirthdate());
            clientDto.setBonusPoints(user.getClientProfile().getBonusPoints());
            clientDto.setNotes(user.getClientProfile().getNotes());
            dto.setClientProfile(clientDto);
        }

        if (user.getMasterProfile() != null) {
            MasterProfileDto masterDto = new MasterProfileDto();
            masterDto.setUserId(user.getUserId());
            masterDto.setSpecialization(user.getMasterProfile().getSpecialization());
            masterDto.setWorkExperience(user.getMasterProfile().getWorkExperience());
            masterDto.setDescription(user.getMasterProfile().getDescription());
            masterDto.setPhotoUrl(user.getMasterProfile().getPhotoUrl());
            masterDto.setIsActive(user.getMasterProfile().getIsActive());
            dto.setMasterProfile(masterDto);
        }

        return dto;
    }

    private String determineUserType(SystemUser user) {
        if (user.getAdminProfile() != null) return "ADMIN";
        if (user.getMasterProfile() != null) return "MASTER";
        if (user.getClientProfile() != null) return "CLIENT";
        return "UNKNOWN";
    }
}