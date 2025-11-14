package Frolov_back.NAILS_WEB_APP.DTO.for_shedule_master;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class WeeklyAdjustmentDto {
    private Long adjustmentId;
    private Long masterId;
    private String masterName;
    private LocalDate weekStartDate;
    private String reason;
    private Map<String, Integer[]> adjustedSchedule;
    private LocalDateTime createdAt;
}
