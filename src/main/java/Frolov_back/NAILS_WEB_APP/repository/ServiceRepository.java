package Frolov_back.NAILS_WEB_APP.repository;

import Frolov_back.NAILS_WEB_APP.domain.Service;
import Frolov_back.NAILS_WEB_APP.domain.ServiceCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends BaseRepository<Service, Long> {

    // Найти по имени
    Optional<Service> findByName(String name);

    // Найти все активные услуги
    List<Service> findByIsActiveTrue();

    // Найти услуги по категории
    List<Service> findByCategoryAndIsActiveTrue(ServiceCategory category);

    // Найти услуги по цене (диапазон)
    List<Service> findByBasePriceBetweenAndIsActiveTrue(BigDecimal minPrice, BigDecimal maxPrice);

    // Найти услуги по длительности (максимум)
    List<Service> findByBaseDurationLessThanEqualAndIsActiveTrue(Integer maxDuration);

    // Поиск услуг по названию (регистронезависимо)
    List<Service> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);

    // Найти популярные услуги (по количеству записей)
    @Query("SELECT s FROM Service s LEFT JOIN s.appointments a WHERE s.isActive = true " +
            "GROUP BY s.serviceId ORDER BY COUNT(a) DESC")
    List<Service> findPopularServices();

    // Найти услуги с мастером
    @Query("SELECT DISTINCT s FROM Service s JOIN s.masterServices ms WHERE s.isActive = true AND ms.master.userId = :masterId")
    List<Service> findServicesByMaster(@Param("masterId") Long masterId);
}