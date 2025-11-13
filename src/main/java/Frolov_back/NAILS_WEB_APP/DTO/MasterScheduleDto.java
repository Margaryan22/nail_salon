package Frolov_back.NAILS_WEB_APP.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalTime;

@Data
@Schema(description = "DTO для расписания мастера")
public class MasterScheduleDto {

    @Schema(description = "ID записи расписания", example = "1")
    private Long scheduleId;

    @Schema(description = "ID мастера", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long masterId;

    @Schema(
            description = "День недели (1-Понедельник, 7-Воскресенье)",
            example = "5",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "1",
            maximum = "7"
    )
    private Short dayOfWeek;

    @Schema(
            description = "Время начала работы",
            example = "09:00:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalTime startTime;

    @Schema(
            description = "Время окончания работы",
            example = "18:00:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalTime endTime;
}