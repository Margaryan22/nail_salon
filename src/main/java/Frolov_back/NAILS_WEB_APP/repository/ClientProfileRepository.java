package Frolov_back.NAILS_WEB_APP.repository;

import Frolov_back.NAILS_WEB_APP.domain.ClientProfile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientProfileRepository extends BaseRepository<ClientProfile, Long> {

    // Найти по ID пользователя
    Optional<ClientProfile> findByUserId(Long userId);

    // Найти клиентов с бонусными баллами больше указанного
    List<ClientProfile> findByBonusPointsGreaterThan(Integer points);

    // Найти по дате рождения
    List<ClientProfile> findByBirthdate(LocalDate birthdate);

    // Найти клиентов с днем рождения в указанном месяце
    @Query("SELECT cp FROM ClientProfile cp WHERE MONTH(cp.birthdate) = :month")
    List<ClientProfile> findByBirthdateMonth(@Param("month") int month);

    // Найти топ клиентов по бонусным баллам
    List<ClientProfile> findTop10ByOrderByBonusPointsDesc();
}