package Frolov_back.NAILS_WEB_APP.impl;

import Frolov_back.NAILS_WEB_APP.domain.UserRoleType;
import Frolov_back.NAILS_WEB_APP.service.RegistrationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RegistrationServiceFactory {

    private final Map<UserRoleType, RegistrationService> registrationServices;

    public RegistrationServiceFactory(List<RegistrationService> registrationServices) {
        // Преобразуем список в Map для быстрого поиска по типу роли
        this.registrationServices = registrationServices.stream()
                .flatMap(service -> {
                    // Для каждого сервиса определяем, какие роли он поддерживает
                    return List.of(UserRoleType.values()).stream()
                            .filter(service::supports)
                            .map(roleType -> Map.entry(roleType, service));
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> existing // В случае конфликта берем существующий
                ));
    }

    /**
     * Возвращает сервис регистрации для указанного типа пользователя
     */
    public RegistrationService getService(UserRoleType roleType) {
        RegistrationService service = registrationServices.get(roleType);
        if (service == null) {
            throw new IllegalArgumentException(
                    String.format("No registration service found for role: %s. Supported roles: %s",
                            roleType, registrationServices.keySet())
            );
        }
        return service;
    }

    /**
     * Проверяет, поддерживается ли указанный тип регистрации
     */
    public boolean supports(UserRoleType roleType) {
        return registrationServices.containsKey(roleType);
    }

    /**
     * Возвращает список поддерживаемых ролей
     */
    public List<UserRoleType> getSupportedRoles() {
        return List.copyOf(registrationServices.keySet());
    }
}