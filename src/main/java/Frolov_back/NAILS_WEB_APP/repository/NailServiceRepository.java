package Frolov_back.NAILS_WEB_APP.repository;

import Frolov_back.NAILS_WEB_APP.domain.NailService;
import Frolov_back.NAILS_WEB_APP.domain.ServiceCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface NailServiceRepository extends BaseRepository<NailService, Long> {

    // Найти по имени
    Optional<NailService> findByName(String name);

    // Найти все активные услуги
    List<NailService> findByActiveTrue();

    // Найти услуги по категории
    List<NailService> findByCategoryAndActiveTrue(ServiceCategory category);

    // Найти услуги по цене (диапазон)
    List<NailService> findByBasePriceBetweenAndActiveTrue(BigDecimal minPrice, BigDecimal maxPrice);

    // Найти услуги по длительности (максимум)
    List<NailService> findByBaseDurationLessThanEqualAndActiveTrue(Integer maxDuration);

    // Поиск услуг по названию (регистронезависимо)
    List<NailService> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    // Найти популярные услуги (по количеству записей)
    @Query("SELECT s FROM NailService s LEFT JOIN s.appointments a WHERE s.active = true " +
            "GROUP BY s.serviceId ORDER BY COUNT(a) DESC")
    List<NailService> findPopularServices();

    // Найти услуги с мастером
    @Query("SELECT DISTINCT s FROM NailService s JOIN s.masterServices ms WHERE s.active = true AND ms.master.userId = :masterId")
    List<NailService> findServicesByMaster(@Param("masterId") Long masterId);

    boolean existsByName(String name);
}