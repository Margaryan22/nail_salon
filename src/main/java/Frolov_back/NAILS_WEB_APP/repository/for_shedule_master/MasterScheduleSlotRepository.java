package Frolov_back.NAILS_WEB_APP.repository.for_shedule_master;

import Frolov_back.NAILS_WEB_APP.domain.for_shedule_master.MasterScheduleSlot;
import Frolov_back.NAILS_WEB_APP.domain.for_shedule_master.MasterWeeklySchedule;
import Frolov_back.NAILS_WEB_APP.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MasterScheduleSlotRepository extends BaseRepository<MasterScheduleSlot, Long> {

    // Найти все слоты для расписания
    List<MasterScheduleSlot> findBySchedule(MasterWeeklySchedule schedule);

    // Найти слоты по дню недели для расписания
    List<MasterScheduleSlot> findByScheduleAndDayOfWeek(MasterWeeklySchedule schedule, Integer dayOfWeek);

    // Удалить все слоты для расписания
    void deleteBySchedule(MasterWeeklySchedule schedule);
}