package Frolov_back.NAILS_WEB_APP.DTO.for_shedule_master;

import lombok.Data;
import java.util.Map;

@Data
public class WeeklyScheduleRequestDto {
    private Long masterId;
    private Map<Integer, Integer[]> dailySlots; // {1: [1,2,3,4,5], 2: [1,2,3]}

    @Override
    public String toString() {
        return "WeeklyScheduleRequestDto{" +
                "masterId=" + masterId +
                ", dailySlots=" + dailySlots +
                '}';
    }
}