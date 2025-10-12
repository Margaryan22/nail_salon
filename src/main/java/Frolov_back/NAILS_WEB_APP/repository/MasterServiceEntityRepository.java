package Frolov_back.NAILS_WEB_APP.repository;

import Frolov_back.NAILS_WEB_APP.domain.MasterServiceEntity;
import Frolov_back.NAILS_WEB_APP.domain.MasterServiceId;
import Frolov_back.NAILS_WEB_APP.domain.Service;
import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MasterServiceEntityRepository extends BaseRepository<MasterServiceEntity, MasterServiceId> {

    // Найти все услуги мастера
    List<MasterServiceEntity> findByMaster(SystemUser master);

    // Найти всех мастеров для услуги
    List<MasterServiceEntity> findByService(Service service);

    // Найти конкретную связь мастер-услуга
    Optional<MasterServiceEntity> findByMasterAndService(SystemUser master, Service service);

    // Найти услуги мастера с ценой в диапазоне
    List<MasterServiceEntity> findByMasterAndMasterPriceBetween(SystemUser master, BigDecimal minPrice, BigDecimal maxPrice);

    // Найти мастеров для услуги с сортировкой по цене
    List<MasterServiceEntity> findByServiceOrderByMasterPriceAsc(Service service);

    // Удалить связь мастер-услуга
    void deleteByMasterAndService(SystemUser master, Service service);

    // Проверить существует ли связь
    boolean existsByMasterAndService(SystemUser master, Service service);

    // Найти среднюю цену услуги у всех мастеров
    @Query("SELECT AVG(ms.masterPrice) FROM MasterServiceEntity ms WHERE ms.service.serviceId = :serviceId")
    Optional<BigDecimal> findAveragePriceByService(@Param("serviceId") Long serviceId);
}