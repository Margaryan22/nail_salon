package Frolov_back.NAILS_WEB_APP.DTO.for_shedule_master.month;

import lombok.Data;
import java.time.YearMonth;
import java.util.Map;

@Data
public class MonthlyScheduleRequestDto {
    private Long masterId;
    private YearMonth month;
    private String templateType; // STANDARD, REDUCED, CUSTOM
    private Map<Integer, Integer[]> dailySlots; // Базовые слоты по дням недели
}
