package Frolov_back.NAILS_WEB_APP.repository.for_shedule_master.month;

import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import Frolov_back.NAILS_WEB_APP.domain.for_shedule_master.month.MasterMonthlySchedule;
import Frolov_back.NAILS_WEB_APP.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface MasterMonthlyScheduleRepository extends BaseRepository<MasterMonthlySchedule, Long> {

    Optional<MasterMonthlySchedule> findByMasterAndScheduleMonth(SystemUser master, YearMonth scheduleMonth);

    List<MasterMonthlySchedule> findByMaster(SystemUser master);

    boolean existsByMasterAndScheduleMonth(SystemUser master, YearMonth scheduleMonth);

    void deleteByMasterAndScheduleMonth(SystemUser master, YearMonth scheduleMonth);
}
