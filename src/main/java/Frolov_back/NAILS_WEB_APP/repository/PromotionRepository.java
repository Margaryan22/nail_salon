package Frolov_back.NAILS_WEB_APP.repository;

import Frolov_back.NAILS_WEB_APP.domain.NailService;
import Frolov_back.NAILS_WEB_APP.domain.Promotion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends BaseRepository<Promotion, Long> {

    // Найти активные акции
    List<Promotion> findByIsActiveTrue();

    // Найти по промокоду
    Optional<Promotion> findByPromoCode(String promoCode);

    // Найти акции для услуги
    List<Promotion> findByServiceAndIsActiveTrue(NailService service);

    // Найти действующие акции на дату
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND " +
            "(p.startDate IS NULL OR p.startDate <= :date) AND " +
            "(p.endDate IS NULL OR p.endDate >= :date)")
    List<Promotion> findActivePromotionsOnDate(@Param("date") LocalDate date);

    // Найти акции по типу скидки
    List<Promotion> findByDiscountTypeAndIsActiveTrue(String discountType);

    // Проверить существование промокода
    boolean existsByPromoCode(String promoCode);

    // Найти истекшие акции
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.endDate < :currentDate")
    List<Promotion> findExpiredPromotions(@Param("currentDate") LocalDate currentDate);
}