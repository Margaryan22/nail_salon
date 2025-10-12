package Frolov_back.NAILS_WEB_APP.repository;

import Frolov_back.NAILS_WEB_APP.domain.AdminProfile;
import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminProfileRepository extends BaseRepository<AdminProfile, Long> {

    // Найти профиль администратора по пользователю
    Optional<AdminProfile> findBySystemUser(SystemUser systemUser);

    // Найти профиль администратора по ID пользователя
    Optional<AdminProfile> findBySystemUserUserId(Long userId);

    // Найти всех администраторов с уровнем прав
    List<AdminProfile> findByPermissionsLevel(Integer permissionsLevel);

    // Найти администраторов с уровнем прав выше указанного
    List<AdminProfile> findByPermissionsLevelGreaterThanEqual(Integer minLevel);

    // Найти супер-администраторов (уровень 2)
    @Query("SELECT ap FROM AdminProfile ap WHERE ap.permissionsLevel = 2")
    List<AdminProfile> findSuperAdmins();

    // Проверить является ли пользователь администратором
    boolean existsBySystemUserUserId(Long userId);

    // Проверить является ли пользователь супер-администратором
    @Query("SELECT COUNT(ap) > 0 FROM AdminProfile ap WHERE ap.systemUser.userId = :userId AND ap.permissionsLevel = 2")
    boolean isSuperAdmin(@Param("userId") Long userId);

    // Получить уровень прав пользователя
    @Query("SELECT ap.permissionsLevel FROM AdminProfile ap WHERE ap.systemUser.userId = :userId")
    Optional<Integer> findPermissionsLevelByUserId(@Param("userId") Long userId);

    // Найти всех администраторов с информацией о пользователе
    @Query("SELECT ap FROM AdminProfile ap JOIN FETCH ap.systemUser ORDER BY ap.permissionsLevel DESC, ap.systemUser.lastName ASC")
    List<AdminProfile> findAllWithUserInfo();
}