package Frolov_back.NAILS_WEB_APP.DTO.for_shedule_master;

import lombok.Data;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Data
public class BulkScheduleRequestDto {
    private Long masterId;
    private List<YearMonth> months;
    private String templateType;
    private Map<Integer, Integer[]> dailySlots;
}
