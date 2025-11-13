package Frolov_back.NAILS_WEB_APP.DTO.for_shedule_master;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class MasterWeeklyScheduleDto {
    private Long scheduleId;
    private Long masterId;
    private String masterName;
    private LocalDate weekStartDate;
    private Map<String, Integer[]> weeklySchedule; // {"Понедельник": [1,2,3,4,5], "Вторник": [1,2,3]}
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}