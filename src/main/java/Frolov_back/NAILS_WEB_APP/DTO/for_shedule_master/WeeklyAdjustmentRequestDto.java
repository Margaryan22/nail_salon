package Frolov_back.NAILS_WEB_APP.DTO.for_shedule_master;

import lombok.Data;
import java.time.LocalDate;
import java.util.Map;

@Data
public class WeeklyAdjustmentRequestDto {
    private Long masterId;
    private LocalDate weekStartDate;
    private String reason;
    private Map<Integer, Integer[]> dailySlots; // Корректировки по дням недели
}
