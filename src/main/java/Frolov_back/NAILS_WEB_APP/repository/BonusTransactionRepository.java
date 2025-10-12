package Frolov_back.NAILS_WEB_APP.repository;

import Frolov_back.NAILS_WEB_APP.domain.BonusTransaction;
import Frolov_back.NAILS_WEB_APP.domain.BonusTransactionType;
import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BonusTransactionRepository extends BaseRepository<BonusTransaction, Long> {

    // Найти транзакции клиента
    List<BonusTransaction> findByClient(SystemUser client);

    // Найти транзакции по типу
    List<BonusTransaction> findByClientAndType(SystemUser client, BonusTransactionType type);

    // Найти транзакции за период
    List<BonusTransaction> findByClientAndCreatedAtBetween(SystemUser client, LocalDateTime start, LocalDateTime end);

    // Посчитать баланс клиента
    @Query("SELECT COALESCE(SUM(CASE WHEN bt.type = 'EARN' THEN bt.points ELSE -bt.points END), 0) " +
            "FROM BonusTransaction bt WHERE bt.client = :client")
    Integer calculateClientBalance(@Param("client") SystemUser client);

    // Найти транзакции по записи
    List<BonusTransaction> findByAppointment_AppointmentId(Long appointmentId);

    // Статистика бонусов по периоду
    @Query("SELECT bt.type, SUM(bt.points) FROM BonusTransaction bt WHERE bt.createdAt BETWEEN :start AND :end GROUP BY bt.type")
    List<Object[]> getBonusStatistics(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}