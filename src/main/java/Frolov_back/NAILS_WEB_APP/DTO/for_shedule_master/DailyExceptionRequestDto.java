package Frolov_back.NAILS_WEB_APP.DTO.for_shedule_master;

import Frolov_back.NAILS_WEB_APP.domain.for_shedule_master.ScheduleExceptionType;
import lombok.Data;
import java.time.LocalDate;

@Data
public class DailyExceptionRequestDto {
    private Long masterId;
    private LocalDate exceptionDate;
    private ScheduleExceptionType exceptionType;
    private Boolean allDay;
    private String reason;
    private Integer[] availableSlots; // Если не весь день выходной
}