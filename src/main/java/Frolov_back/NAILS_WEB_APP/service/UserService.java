package Frolov_back.NAILS_WEB_APP.service;

import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import Frolov_back.NAILS_WEB_APP.service.DTO.*;

import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import Frolov_back.NAILS_WEB_APP.service.DTO.*;

import java.util.List;
import java.util.Optional;

public interface UserService {

    // === УДАЛЕНО: все методы регистрации ===
    // Optional<UserResponseDto> registerNewUser(UserRegistrationRequestDto requestDto);
    // Optional<UserResponseDto> registerClient(ClientRegistrationRequestDto requestDto);
    // Optional<UserResponseDto> registerMaster(MasterRegistrationRequestDto requestDto);

    // === Поиск и получение (ОСТАВЛЯЕМ) ===
    Optional<SystemUser> findUserByEmail(String email);
    Optional<UserResponseDto> getUserById(Long userId);
    Optional<UserWithProfileDto> getUserWithProfile(Long userId);
    List<UserResponseDto> getAllUsers();
    List<UserResponseDto> getUsersByRole(String role);
    List<UserResponseDto> searchUsers(UserSearchCriteriaDto criteria);

    // === Обновление (ОСТАВЛЯЕМ) ===
    Optional<UserResponseDto> updateUser(Long userId, UserUpdateRequestDto requestDto);
    Optional<UserResponseDto> updateClientProfile(Long userId, ClientProfileDto profileDto);
    Optional<UserResponseDto> updateMasterProfile(Long userId, MasterProfileDto profileDto);
    Optional<UserResponseDto> getCurrentUser(String email);

    // === Админские функции (ОСТАВЛЯЕМ) ===
    List<UserResponseDto> getAllAdmins();
    Optional<UserResponseDto> promoteToSuperAdmin(Long userId);
    Optional<UserResponseDto> demoteToRegularAdmin(Long userId);
    boolean isSuperAdmin(Long userId);

    // === Управление активностью (ОСТАВЛЯЕМ) ===
    boolean deactivateUser(Long userId);
    boolean activateUser(Long userId);

    // === Валидация (ОСТАВЛЯЕМ) ===
    boolean isEmailAvailable(String email);
    boolean isPhoneAvailable(String phone);
}