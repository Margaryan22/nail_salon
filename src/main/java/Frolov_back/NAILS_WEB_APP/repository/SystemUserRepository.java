package Frolov_back.NAILS_WEB_APP.repository;

import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import Frolov_back.NAILS_WEB_APP.domain.UserRoleType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemUserRepository extends BaseRepository<SystemUser, Long> {

    // Найти по email
    Optional<SystemUser> findByEmail(String email);

    // Проверить существование по email
    boolean existsByEmail(String email);

    // Найти всех по роли
    List<SystemUser> findByRole(UserRoleType role);

    // Найти по имени и фамилии
    List<SystemUser> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(String firstName, String lastName);

    // Найти по телефону
    Optional<SystemUser> findByPhone(String phone);

    // Найти активных мастеров
    @Query("SELECT u FROM SystemUser u JOIN u.masterProfile mp WHERE u.role = 'MASTER' AND mp.isActive = true")
    List<SystemUser> findActiveMasters();

    // Найти по email с игнорированием регистра
    Optional<SystemUser> findByEmailIgnoreCase(String email);

    // Проверить существует ли телефон
    boolean existsByPhone(String phone);
}
