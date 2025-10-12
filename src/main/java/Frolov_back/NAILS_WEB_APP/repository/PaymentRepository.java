package Frolov_back.NAILS_WEB_APP.repository;

import Frolov_back.NAILS_WEB_APP.domain.Payment;
import Frolov_back.NAILS_WEB_APP.domain.PaymentStatusType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends BaseRepository<Payment, Long> {

    // Найти платеж по записи
    Optional<Payment> findByAppointment_AppointmentId(Long appointmentId);

    // Найти платежи по статусу
    List<Payment> findByStatus(PaymentStatusType status);

    // Найти платежи за период
    List<Payment> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Найти завершенные платежи
    List<Payment> findByStatusAndPaidAtBetween(PaymentStatusType status, LocalDateTime start, LocalDateTime end);

    // Найти по ID транзакции
    Optional<Payment> findByTransactionId(String transactionId);

    // Статистика платежей по методу
    @Query("SELECT p.paymentMethod, COUNT(p), SUM(p.amount) FROM Payment p WHERE p.createdAt BETWEEN :start AND :end AND p.status = 'COMPLETED' GROUP BY p.paymentMethod")
    List<Object[]> getPaymentStatistics(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Найти платежи ожидающие обработки
    List<Payment> findByStatusOrderByCreatedAtAsc(PaymentStatusType status);
}