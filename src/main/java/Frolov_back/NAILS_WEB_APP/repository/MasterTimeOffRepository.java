package Frolov_back.NAILS_WEB_APP.repository;

import Frolov_back.NAILS_WEB_APP.domain.MasterTimeOff;
import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MasterTimeOffRepository extends BaseRepository<MasterTimeOff, Long> {

    // Найти выходные мастера
    List<MasterTimeOff> findByMaster(SystemUser master);

    // Найти будущие выходные мастера
    List<MasterTimeOff> findByMasterAndStartDatetimeAfter(SystemUser master, LocalDateTime dateTime);

    // Найти выходные в указанный период
    @Query("SELECT mto FROM MasterTimeOff mto WHERE mto.master = :master AND " +
            "((mto.startDatetime BETWEEN :start AND :end) OR (mto.endDatetime BETWEEN :start AND :end))")
    List<MasterTimeOff> findTimeOffsInPeriod(@Param("master") SystemUser master,
                                             @Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);

    // Проверить пересечение с выходными
    @Query("SELECT COUNT(mto) > 0 FROM MasterTimeOff mto WHERE mto.master = :master AND " +
            "((mto.startDatetime <= :endDateTime AND mto.endDatetime >= :startDateTime))")
    boolean existsTimeOffConflict(@Param("master") SystemUser master,
                                  @Param("startDateTime") LocalDateTime startDateTime,
                                  @Param("endDateTime") LocalDateTime endDateTime);
}