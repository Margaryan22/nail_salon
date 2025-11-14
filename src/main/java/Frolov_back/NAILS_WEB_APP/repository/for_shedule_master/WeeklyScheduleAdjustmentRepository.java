package Frolov_back.NAILS_WEB_APP.repository.for_shedule_master;

import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import Frolov_back.NAILS_WEB_APP.domain.for_shedule_master.WeeklyScheduleAdjustment;
import Frolov_back.NAILS_WEB_APP.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyScheduleAdjustmentRepository extends BaseRepository<WeeklyScheduleAdjustment, Long> {

    Optional<WeeklyScheduleAdjustment> findByMasterAndWeekStartDate(SystemUser master, LocalDate weekStartDate);

    List<WeeklyScheduleAdjustment> findByMaster(SystemUser master);

    @Query("SELECT wsa FROM WeeklyScheduleAdjustment wsa WHERE wsa.master = :master AND wsa.weekStartDate <= :date AND wsa.weekStartDate >= :weekStart")
    List<WeeklyScheduleAdjustment> findAdjustmentsForDate(@Param("master") SystemUser master,
                                                          @Param("date") LocalDate date,
                                                          @Param("weekStart") LocalDate weekStart);

    boolean existsByMasterAndWeekStartDate(SystemUser master, LocalDate weekStartDate);
}
