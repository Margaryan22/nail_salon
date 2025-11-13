package Frolov_back.NAILS_WEB_APP.controller;

import Frolov_back.NAILS_WEB_APP.service.for_shedule_master.MasterScheduleService;
import Frolov_back.NAILS_WEB_APP.DTO.for_shedule_master.MasterWeeklyScheduleDto;
import Frolov_back.NAILS_WEB_APP.DTO.for_shedule_master.WeeklyScheduleRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/master-schedule")
@RequiredArgsConstructor
@Tag(
        name = "📅 Управление расписанием мастеров",
        description = "API для работы с недельным расписанием мастеров"
)
public class MasterScheduleController {

    private final MasterScheduleService masterScheduleService;

    @Operation(
            summary = "📋 Создать или обновить недельное расписание",
            description = "Создает новое расписание на текущую неделю или обновляет существующее"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Расписание успешно создано/обновлено"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "❌ Мастер не найден"
            )
    })
    @PostMapping("/weekly")
    public ResponseEntity<MasterWeeklyScheduleDto> createOrUpdateWeeklySchedule(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания расписания",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = WeeklyScheduleRequestDto.class),
                            examples = @ExampleObject(
                                    value = """
                        {
                            "masterId": 3,
                            "dailySlots": {
                                "1": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
                                "2": [1, 2, 3, 4, 5],
                                "3": [6, 7, 8, 9, 10],
                                "4": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
                                "5": [1, 2, 3, 4, 5],
                                "6": [6, 7, 8]
                            }
                        }
                        """
                            )
                    )
            )
            @RequestBody WeeklyScheduleRequestDto requestDto) {

        MasterWeeklyScheduleDto schedule = masterScheduleService.createOrUpdateWeeklySchedule(requestDto);
        return ResponseEntity.ok(schedule);
    }

    @Operation(
            summary = "🔍 Получить расписание на неделю",
            description = "Возвращает расписание мастера на указанную неделю"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Расписание найдено"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "❌ Расписание не найдено"
            )
    })
    @GetMapping("/master/{masterId}/week")
    public ResponseEntity<MasterWeeklyScheduleDto> getWeeklySchedule(
            @Parameter(description = "ID мастера", example = "3", required = true)
            @PathVariable Long masterId,

            @Parameter(description = "Дата начала недели (понедельник)", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStartDate) {

        MasterWeeklyScheduleDto schedule = masterScheduleService.getWeeklySchedule(masterId, weekStartDate);
        return ResponseEntity.ok(schedule);
    }

    @Operation(
            summary = "📅 Получить текущее расписание",
            description = "Возвращает текущее расписание мастера на эту неделю"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Текущее расписание найдено"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "❌ Текущее расписание не найдено"
            )
    })
    @GetMapping("/master/{masterId}/current")
    public ResponseEntity<MasterWeeklyScheduleDto> getCurrentWeeklySchedule(
            @Parameter(description = "ID мастера", example = "3", required = true)
            @PathVariable Long masterId) {

        MasterWeeklyScheduleDto schedule = masterScheduleService.getCurrentWeeklySchedule(masterId);
        return ResponseEntity.ok(schedule);
    }

    @Operation(
            summary = "📚 Получить все расписания мастера",
            description = "Возвращает все расписания мастера"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Список расписаний получен"
            )
    })
    @GetMapping("/master/{masterId}/all")
    public ResponseEntity<List<MasterWeeklyScheduleDto>> getAllMasterSchedules(
            @Parameter(description = "ID мастера", example = "3", required = true)
            @PathVariable Long masterId) {

        List<MasterWeeklyScheduleDto> schedules = masterScheduleService.getAllMasterSchedules(masterId);
        return ResponseEntity.ok(schedules);
    }

    @Operation(
            summary = "✅ Проверить доступность слота",
            description = "Проверяет доступен ли временной слот у мастера на указанную дату"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Проверка выполнена"
            )
    })
    @GetMapping("/availability/check")
    public ResponseEntity<Boolean> checkTimeSlotAvailability(
            @Parameter(description = "ID мастера", example = "3", required = true)
            @RequestParam Long masterId,

            @Parameter(description = "Дата для проверки", example = "2024-01-15", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,

            @Parameter(description = "Номер слота (1-10)", example = "3", required = true)
            @RequestParam Integer timeSlot) {

        boolean available = masterScheduleService.isTimeSlotAvailable(masterId, date, timeSlot);
        return ResponseEntity.ok(available);
    }

    @Operation(
            summary = "🕒 Получить доступные слоты на день",
            description = "Возвращает список доступных временных слотов мастера на указанную дату"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Список слотов получен"
            )
    })
    @GetMapping("/availability/day")
    public ResponseEntity<List<Integer>> getAvailableSlotsForDay(
            @Parameter(description = "ID мастера", example = "3", required = true)
            @RequestParam Long masterId,

            @Parameter(description = "Дата", example = "2024-01-15", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<Integer> availableSlots = masterScheduleService.getAvailableSlotsForDay(masterId, date);
        return ResponseEntity.ok(availableSlots);
    }

    @Operation(
            summary = "🗑️ Удалить расписание на неделю",
            description = "Удаляет расписание мастера на указанную неделю"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Расписание удалено"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "❌ Расписание не найдено"
            )
    })
    @DeleteMapping("/master/{masterId}/week")
    public ResponseEntity<Void> deleteWeeklySchedule(
            @Parameter(description = "ID мастера", example = "3", required = true)
            @PathVariable Long masterId,

            @Parameter(description = "Дата начала недели", example = "2024-01-01", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStartDate) {

        masterScheduleService.deleteWeeklySchedule(masterId, weekStartDate);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "ℹ️ Получить информацию о временных слотах",
            description = "Возвращает описание всех доступных временных слотов"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Информация о слотах получена"
            )
    })
    @GetMapping("/time-slots-info")
    public ResponseEntity<List<TimeSlotInfo>> getTimeSlotsInfo() {
        List<TimeSlotInfo> slotsInfo = List.of(
                new TimeSlotInfo(1, "09:00", "10:00"),
                new TimeSlotInfo(2, "10:00", "11:00"),
                new TimeSlotInfo(3, "11:00", "12:00"),
                new TimeSlotInfo(4, "12:00", "13:00"),
                new TimeSlotInfo(5, "13:00", "14:00"),
                new TimeSlotInfo(6, "14:00", "15:00"),
                new TimeSlotInfo(7, "15:00", "16:00"),
                new TimeSlotInfo(8, "16:00", "17:00"),
                new TimeSlotInfo(9, "17:00", "18:00"),
                new TimeSlotInfo(10, "18:00", "19:00")
        );
        return ResponseEntity.ok(slotsInfo);
    }

    @Operation(summary = "📅 Создать стандартное расписание")
    @PostMapping("/master/{masterId}/default-schedule")
    public ResponseEntity<MasterWeeklyScheduleDto> createDefaultSchedule(
            @Parameter(description = "ID мастера", example = "5", required = true)
            @PathVariable Long masterId) {  // Меняем @RequestParam на @PathVariable

        WeeklyScheduleRequestDto requestDto = new WeeklyScheduleRequestDto();
        requestDto.setMasterId(masterId);  // Используем masterId из пути

        // Стандартное расписание: Пн-Пт 9:00-18:00, Сб 10:00-16:00
        Map<Integer, Integer[]> defaultSlots = Map.of(
                1, new Integer[]{1,2,3,4,5,6,7,8,9,10}, // Пн
                2, new Integer[]{1,2,3,4,5,6,7,8,9,10}, // Вт
                3, new Integer[]{1,2,3,4,5,6,7,8,9,10}, // Ср
                4, new Integer[]{1,2,3,4,5,6,7,8,9,10}, // Чт
                5, new Integer[]{1,2,3,4,5,6,7,8,9,10}, // Пт
                6, new Integer[]{2,3,4,5,6,7,8}         // Сб (10:00-16:00)
        );

        requestDto.setDailySlots(defaultSlots);

        MasterWeeklyScheduleDto schedule = masterScheduleService.createOrUpdateWeeklySchedule(requestDto);
        return ResponseEntity.ok(schedule);
    }

    // Вспомогательный класс для информации о слотах
    @Schema(description = "Информация о временном слоте")
    public static class TimeSlotInfo {
        @Schema(description = "Номер слота", example = "1")
        private final int slotNumber;

        @Schema(description = "Время начала", example = "09:00")
        private final String startTime;

        @Schema(description = "Время окончания", example = "10:00")
        private final String endTime;

        public TimeSlotInfo(int slotNumber, String startTime, String endTime) {
            this.slotNumber = slotNumber;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        // геттеры
        public int getSlotNumber() { return slotNumber; }
        public String getStartTime() { return startTime; }
        public String getEndTime() { return endTime; }
    }
}