package Frolov_back.NAILS_WEB_APP.repository;

import Frolov_back.NAILS_WEB_APP.domain.Appointment;
import Frolov_back.NAILS_WEB_APP.domain.AppointmentStatusType;
import Frolov_back.NAILS_WEB_APP.domain.Service;
import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends BaseRepository<Appointment, Long> {

    // Найти записи клиента
    List<Appointment> findByClient(SystemUser client);

    // Найти записи мастера
    List<Appointment> findByMaster(SystemUser master);

    // Найти записи по статусу
    List<Appointment> findByStatus(AppointmentStatusType status);

    // Найти записи клиента по статусу
    List<Appointment> findByClientAndStatus(SystemUser client, AppointmentStatusType status);

    // Найти записи мастера по статусу
    List<Appointment> findByMasterAndStatus(SystemUser master, AppointmentStatusType status);

    // Найти записи в указанный период
    List<Appointment> findByAppointmentDatetimeBetween(LocalDateTime start, LocalDateTime end);

    // Найти записи мастера в указанный период
    List<Appointment> findByMasterAndAppointmentDatetimeBetween(SystemUser master, LocalDateTime start, LocalDateTime end);

    // Найти будущие записи
    List<Appointment> findByAppointmentDatetimeAfter(LocalDateTime dateTime);

    // Найти прошедшие записи
    List<Appointment> findByAppointmentDatetimeBefore(LocalDateTime dateTime);

    // Проверить доступность времени у мастера
    @Query("SELECT COUNT(a) = 0 FROM Appointment a WHERE a.master = :master AND " +
            "a.status NOT IN ('CANCELLED_BY_CLIENT', 'CANCELLED_BY_SALON') AND " +
            "((a.appointmentDatetime <= :endDateTime AND a.endDatetime >= :startDateTime))")
    boolean isTimeSlotAvailable(@Param("master") SystemUser master,
                                @Param("startDateTime") LocalDateTime startDateTime,
                                @Param("endDateTime") LocalDateTime endDateTime);

    // Найти записи для отзыва (завершенные без отзыва)
    @Query("SELECT a FROM Appointment a WHERE a.client = :client AND a.status = 'COMPLETED' AND a.review IS NULL")
    List<Appointment> findCompletedAppointmentsWithoutReview(@Param("client") SystemUser client);

    // Статистика по услугам
    @Query("SELECT a.service, COUNT(a) FROM Appointment a WHERE a.appointmentDatetime BETWEEN :start AND :end GROUP BY a.service")
    List<Object[]> getServiceStatistics(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}