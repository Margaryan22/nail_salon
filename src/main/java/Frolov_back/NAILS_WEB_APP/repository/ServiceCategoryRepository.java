package Frolov_back.NAILS_WEB_APP.repository;

import Frolov_back.NAILS_WEB_APP.domain.ServiceCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceCategoryRepository extends BaseRepository<ServiceCategory, Long> {

    // Найти по имени
    Optional<ServiceCategory> findByName(String name);

    // Найти по имени (регистронезависимо)
    Optional<ServiceCategory> findByNameIgnoreCase(String name);

    // Найти все категории с сортировкой
    List<ServiceCategory> findAllByOrderBySortOrderAsc();

    // Найти категории с услугами
    @Query("SELECT DISTINCT sc FROM ServiceCategory sc JOIN sc.services s WHERE s.isActive = true")
    List<ServiceCategory> findCategoriesWithActiveServices();

    // Проверить существование по имени
    boolean existsByName(String name);
}