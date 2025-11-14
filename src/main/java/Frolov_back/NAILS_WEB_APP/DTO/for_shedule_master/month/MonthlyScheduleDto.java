package Frolov_back.NAILS_WEB_APP.DTO.for_shedule_master.month;

import lombok.Data;
import java.time.YearMonth;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class MonthlyScheduleDto {
    private Long monthlyScheduleId;
    private Long masterId;
    private String masterName;
    private YearMonth month;
    private String templateType;
    private Map<String, Integer[]> monthlySchedule; // {"Понедельник": [1,2,3], ...}
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}