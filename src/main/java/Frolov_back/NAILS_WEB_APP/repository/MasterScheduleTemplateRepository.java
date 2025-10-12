package Frolov_back.NAILS_WEB_APP.repository;

import Frolov_back.NAILS_WEB_APP.domain.MasterScheduleTemplate;
import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MasterScheduleTemplateRepository extends BaseRepository<MasterScheduleTemplate, Long> {

    // Найти расписание мастера
    List<MasterScheduleTemplate> findByMaster(SystemUser master);

    // Найти расписание по дню недели
    List<MasterScheduleTemplate> findByMasterAndDayOfWeek(SystemUser master, Short dayOfWeek);

    // Найти расписание мастера с сортировкой
    List<MasterScheduleTemplate> findByMasterOrderByDayOfWeekAscStartTimeAsc(SystemUser master);

    // Удалить все расписание мастера
    void deleteByMaster(SystemUser master);
}