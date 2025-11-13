package Frolov_back.NAILS_WEB_APP.repository.for_shedule_master;

import Frolov_back.NAILS_WEB_APP.domain.for_shedule_master.MasterWeeklySchedule;
import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import Frolov_back.NAILS_WEB_APP.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MasterWeeklyScheduleRepository extends BaseRepository<MasterWeeklySchedule, Long> {

    Optional<MasterWeeklySchedule> findByMasterAndWeekStartDate(SystemUser master, LocalDate weekStartDate);

    List<MasterWeeklySchedule> findByMaster(SystemUser master);

    // УПРОЩАЕМ ЗАПРОС - убираем сложные JOIN'ы
    @Query("SELECT mws FROM MasterWeeklySchedule mws WHERE mws.master = :master AND mws.weekStartDate = :weekStart")
    Optional<MasterWeeklySchedule> findCurrentSchedule(@Param("master") SystemUser master,
                                                       @Param("weekStart") LocalDate weekStart);

    boolean existsByMasterAndWeekStartDate(SystemUser master, LocalDate weekStartDate);

    void deleteByMasterAndWeekStartDate(SystemUser master, LocalDate weekStartDate);
}