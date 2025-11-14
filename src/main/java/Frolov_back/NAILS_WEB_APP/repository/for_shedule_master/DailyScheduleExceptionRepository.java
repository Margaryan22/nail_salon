package Frolov_back.NAILS_WEB_APP.repository.for_shedule_master;

import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import Frolov_back.NAILS_WEB_APP.domain.for_shedule_master.DailyScheduleException;
import Frolov_back.NAILS_WEB_APP.domain.for_shedule_master.ScheduleExceptionType;
import Frolov_back.NAILS_WEB_APP.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyScheduleExceptionRepository extends BaseRepository<DailyScheduleException, Long> {

    Optional<DailyScheduleException> findByMasterAndExceptionDate(SystemUser master, LocalDate exceptionDate);

    List<DailyScheduleException> findByMasterAndExceptionDateBetween(SystemUser master, LocalDate startDate, LocalDate endDate);

    List<DailyScheduleException> findByMasterAndExceptionType(SystemUser master, ScheduleExceptionType exceptionType);

    @Query("SELECT dse FROM DailyScheduleException dse WHERE dse.master = :master AND dse.exceptionDate = :date")
    Optional<DailyScheduleException> findExceptionForDate(@Param("master") SystemUser master,
                                                          @Param("date") LocalDate date);

    boolean existsByMasterAndExceptionDate(SystemUser master, LocalDate exceptionDate);
}
