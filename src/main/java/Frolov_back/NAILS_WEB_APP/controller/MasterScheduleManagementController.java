package Frolov_back.NAILS_WEB_APP.controller;

import Frolov_back.NAILS_WEB_APP.DTO.for_shedule_master.*;
import Frolov_back.NAILS_WEB_APP.DTO.for_shedule_master.month.MonthlyScheduleDto;
import Frolov_back.NAILS_WEB_APP.DTO.for_shedule_master.month.MonthlyScheduleRequestDto;
import Frolov_back.NAILS_WEB_APP.service.for_shedule_master.MasterScheduleManagementService;
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
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/schedule-management")
@RequiredArgsConstructor
@Tag(
        name = "🗓️ Управление расписанием мастеров",
        description = "API для многоуровневого управления расписанием (месячное, недельное, дневное)"
)
public class MasterScheduleManagementController {

    private final MasterScheduleManagementService scheduleManagementService;

    // === МЕСЯЧНОЕ РАСПИСАНИЕ ===
    @Operation(
            summary = "📅 Создать месячное расписание",
            description = "Создает базовое расписание мастера на указанный месяц"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "✅ Месячное расписание создано"),
            @ApiResponse(responseCode = "400", description = "❌ Ошибка валидации данных"),
            @ApiResponse(responseCode = "404", description = "❌ Мастер не найден")
    })
    @PostMapping("/monthly")
    public ResponseEntity<MonthlyScheduleDto> createMonthlySchedule(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания месячного расписания",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = MonthlyScheduleRequestDto.class),
                            examples = @ExampleObject(
                                    value = """
                        {
                            "masterId": 5,
                            "month": "2024-01",
                            "templateType": "STANDARD",
                            "dailySlots": {
                                "1": [1,2,3,4,5,6,7,8,9,10],
                                "2": [1,2,3,4,5,6,7,8,9,10],
                                "3": [1,2,3,4,5,6,7,8,9,10],
                                "4": [1,2,3,4,5,6,7,8,9,10],
                                "5": [1,2,3,4,5,6,7,8,9,10],
                                "6": [2,3,4,5,6,7,8]
                            }
                        }
                        """
                            )
                    )
            )
            @RequestBody MonthlyScheduleRequestDto requestDto) {

        MonthlyScheduleDto schedule = scheduleManagementService.createMonthlySchedule(requestDto);
        return ResponseEntity.ok(schedule);
    }

    @Operation(summary = "Получить месячное расписание")
    @GetMapping("/monthly/master/{masterId}")
    public ResponseEntity<MonthlyScheduleDto> getMonthlySchedule(
            @Parameter(description = "ID мастера", example = "5", required = true)
            @PathVariable Long masterId,

            @Parameter(description = "Месяц в формате YYYY-MM", example = "2024-01", required = true)
            @RequestParam YearMonth month) {

        MonthlyScheduleDto schedule = scheduleManagementService.getMonthlySchedule(masterId, month);
        return ResponseEntity.ok(schedule);
    }

    @Operation(summary = "Удалить месячное расписание")
    @DeleteMapping("/monthly/master/{masterId}")
    public ResponseEntity<Void> deleteMonthlySchedule(
            @PathVariable Long masterId,
            @RequestParam YearMonth month) {

        scheduleManagementService.deleteMonthlySchedule(masterId, month);
        return ResponseEntity.ok().build();
    }

    // === НЕДЕЛЬНЫЕ КОРРЕКТИРОВКИ ===
    @Operation(summary = "📝 Создать недельную корректировку")
    @PostMapping("/weekly-adjustment")
    public ResponseEntity<WeeklyAdjustmentDto> createWeeklyAdjustment(
            @RequestBody WeeklyAdjustmentRequestDto requestDto) {

        WeeklyAdjustmentDto adjustment = scheduleManagementService.createWeeklyAdjustment(requestDto);
        return ResponseEntity.ok(adjustment);
    }

    @Operation(summary = "Получить недельную корректировку")
    @GetMapping("/weekly-adjustment/master/{masterId}")
    public ResponseEntity<WeeklyAdjustmentDto> getWeeklyAdjustment(
            @PathVariable Long masterId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStartDate) {

        WeeklyAdjustmentDto adjustment = scheduleManagementService.getWeeklyAdjustment(masterId, weekStartDate);
        return ResponseEntity.ok(adjustment);
    }

    @Operation(summary = "Удалить недельную корректировку")
    @DeleteMapping("/weekly-adjustment/master/{masterId}")
    public ResponseEntity<Void> deleteWeeklyAdjustment(
            @PathVariable Long masterId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStartDate) {

        scheduleManagementService.deleteWeeklyAdjustment(masterId, weekStartDate);
        return ResponseEntity.ok().build();
    }

    // === ДНЕВНЫЕ ИСКЛЮЧЕНИЯ ===
    @Operation(summary = "🚫 Создать дневное исключение")
    @PostMapping("/daily-exception")
    public ResponseEntity<DailyExceptionDto> createDailyException(
            @RequestBody DailyExceptionRequestDto requestDto) {

        DailyExceptionDto exception = scheduleManagementService.createDailyException(requestDto);
        return ResponseEntity.ok(exception);
    }

    @Operation(summary = "Получить исключения за месяц")
    @GetMapping("/daily-exception/master/{masterId}/month")
    public ResponseEntity<List<DailyExceptionDto>> getMonthlyExceptions(
            @PathVariable Long masterId,
            @RequestParam YearMonth month) {

        List<DailyExceptionDto> exceptions = scheduleManagementService.getMonthlyExceptions(masterId, month);
        return ResponseEntity.ok(exceptions);
    }

    @Operation(summary = "Удалить дневное исключение")
    @DeleteMapping("/daily-exception/{exceptionId}")
    public ResponseEntity<Void> deleteDailyException(@PathVariable Long exceptionId) {
        scheduleManagementService.deleteDailyException(exceptionId);
        return ResponseEntity.ok().build();
    }

    // === МАССОВЫЕ ОПЕРАЦИИ ===
    @Operation(summary = "📊 Создать расписание на несколько месяцев")
    @PostMapping("/bulk-monthly")
    public ResponseEntity<List<MonthlyScheduleDto>> createBulkMonthlySchedules(
            @RequestBody BulkScheduleRequestDto requestDto) {

        List<MonthlyScheduleDto> schedules = scheduleManagementService.createBulkMonthlySchedules(requestDto);
        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "📋 Копировать расписание месяца")
    @PostMapping("/copy-month")
    public ResponseEntity<MonthlyScheduleDto> copyMonthSchedule(
            @RequestParam Long masterId,
            @RequestParam YearMonth sourceMonth,
            @RequestParam YearMonth targetMonth) {

        MonthlyScheduleDto schedule = scheduleManagementService.copyMonthSchedule(masterId, sourceMonth, targetMonth);
        return ResponseEntity.ok(schedule);
    }

    // === ПРОВЕРКА ДОСТУПНОСТИ ===
    @Operation(summary = "✅ Проверить доступность слота")
    @GetMapping("/availability/check")
    public ResponseEntity<Boolean> checkTimeSlotAvailability(
            @RequestParam Long masterId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Integer timeSlot) {

        boolean available = scheduleManagementService.isTimeSlotAvailable(masterId, date, timeSlot);
        return ResponseEntity.ok(available);
    }

    @Operation(summary = "🕒 Получить доступные слоты на день")
    @GetMapping("/availability/day")
    public ResponseEntity<List<Integer>> getAvailableSlotsForDay(
            @RequestParam Long masterId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<Integer> availableSlots = scheduleManagementService.getAvailableSlotsForDay(masterId, date);
        return ResponseEntity.ok(availableSlots);
    }

    // === СЛУЖЕБНЫЕ МЕТОДЫ ===
    @Operation(summary = "🎯 Создать стандартное расписание на текущий и следующий месяц")
    @PostMapping("/master/{masterId}/standard-setup")
    public ResponseEntity<Map<String, Object>> createStandardSetup(
            @PathVariable Long masterId) {

        YearMonth currentMonth = YearMonth.now();
        YearMonth nextMonth = currentMonth.plusMonths(1);

        // Стандартное расписание
        Map<Integer, Integer[]> standardSlots = Map.of(
                1, new Integer[]{1,2,3,4,5,6,7,8,9,10},
                2, new Integer[]{1,2,3,4,5,6,7,8,9,10},
                3, new Integer[]{1,2,3,4,5,6,7,8,9,10},
                4, new Integer[]{1,2,3,4,5,6,7,8,9,10},
                5, new Integer[]{1,2,3,4,5,6,7,8,9,10},
                6, new Integer[]{2,3,4,5,6,7,8}
        );

        BulkScheduleRequestDto bulkRequest = new BulkScheduleRequestDto();
        bulkRequest.setMasterId(masterId);
        bulkRequest.setMonths(List.of(currentMonth, nextMonth));
        bulkRequest.setTemplateType("STANDARD");
        bulkRequest.setDailySlots(standardSlots);

        List<MonthlyScheduleDto> createdSchedules = scheduleManagementService.createBulkMonthlySchedules(bulkRequest);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Стандартное расписание создано на " + createdSchedules.size() + " месяцев",
                "createdSchedules", createdSchedules
        ));
    }
}
