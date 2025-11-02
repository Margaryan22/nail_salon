package Frolov_back.NAILS_WEB_APP.controller;

import Frolov_back.NAILS_WEB_APP.service.MasterScheduleService;
import Frolov_back.NAILS_WEB_APP.service.DTO.MasterScheduleDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/master-schedule")
@RequiredArgsConstructor
@Tag(
        name = "6. 📅 Расписание мастеров",
        description = "API для управления рабочим расписанием мастеров"
)
public class MasterScheduleController {

    private final MasterScheduleService masterScheduleService;

    @Operation(
            summary = "📋 Получить расписание мастера",
            description = "Возвращает полное расписание работы мастера по дням недели"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Расписание успешно получено",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MasterScheduleDto.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "❌ Мастер не найден"
            )
    })
    @GetMapping("/master/{masterId}")
    public ResponseEntity<List<MasterScheduleDto>> getMasterSchedule(
            @Parameter(description = "ID мастера", example = "3", required = true)
            @PathVariable Long masterId) {
        List<MasterScheduleDto> schedule = masterScheduleService.getMasterSchedule(masterId);
        return ResponseEntity.ok(schedule);
    }

    @Operation(
            summary = "➕ Создать запись в расписании",
            description = "Добавляет новую запись о рабочем времени мастера в определенный день недели"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Запись в расписании успешно создана"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "❌ Ошибка валидации данных"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "❌ Мастер не найден"
            )
    })
    @PostMapping
    public ResponseEntity<MasterScheduleDto> createSchedule(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания записи расписания",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = MasterScheduleDto.class),
                            examples = @ExampleObject(
                                    value = """
                        {
                            "masterId": 3,
                            "dayOfWeek": 5,
                            "startTime": "09:00:00",
                            "endTime": "18:00:00"
                        }
                        """
                            )
                    )
            )
            @RequestBody MasterScheduleDto scheduleDto) {
        MasterScheduleDto created = masterScheduleService.createSchedule(scheduleDto);
        return ResponseEntity.ok(created);
    }

    @Operation(
            summary = "🔄 Установить полное расписание мастера",
            description = """
            Полностью заменяет текущее расписание мастера новым.
            Удобно для первоначальной настройки расписания на неделю.
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Расписание успешно установлено"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "❌ Ошибка валидации данных"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "❌ Мастер не найден"
            )
    })
    @PostMapping("/master/{masterId}/set-schedule")
    public ResponseEntity<Void> setMasterSchedule(
            @Parameter(description = "ID мастера", example = "3", required = true)
            @PathVariable Long masterId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Список записей расписания на неделю",
                    required = true,
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = MasterScheduleDto.class)),
                            examples = @ExampleObject(
                                    value = """
                        [
                            {
                                "dayOfWeek": 1,
                                "startTime": "09:00:00",
                                "endTime": "18:00:00"
                            },
                            {
                                "dayOfWeek": 2,
                                "startTime": "09:00:00",
                                "endTime": "18:00:00"
                            },
                            {
                                "dayOfWeek": 3,
                                "startTime": "09:00:00",
                                "endTime": "18:00:00"
                            },
                            {
                                "dayOfWeek": 4,
                                "startTime": "09:00:00",
                                "endTime": "18:00:00"
                            },
                            {
                                "dayOfWeek": 5,
                                "startTime": "09:00:00",
                                "endTime": "18:00:00"
                            },
                            {
                                "dayOfWeek": 6,
                                "startTime": "10:00:00",
                                "endTime": "16:00:00"
                            }
                        ]
                        """
                            )
                    )
            )
            @RequestBody List<MasterScheduleDto> scheduleDtos) {
        masterScheduleService.setMasterSchedule(masterId, scheduleDtos);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "✏️ Обновить запись в расписании",
            description = "Обновляет существующую запись расписания мастера"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Запись расписания успешно обновлена"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "❌ Запись расписания не найдена"
            )
    })
    @PutMapping("/{scheduleId}")
    public ResponseEntity<MasterScheduleDto> updateSchedule(
            @Parameter(description = "ID записи расписания", example = "1", required = true)
            @PathVariable Long scheduleId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новые данные для записи расписания",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = MasterScheduleDto.class),
                            examples = @ExampleObject(
                                    value = """
                        {
                            "dayOfWeek": 5,
                            "startTime": "10:00:00",
                            "endTime": "19:00:00"
                        }
                        """
                            )
                    )
            )
            @RequestBody MasterScheduleDto scheduleDto) {
        MasterScheduleDto updated = masterScheduleService.updateSchedule(scheduleId, scheduleDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "🗑️ Удалить запись из расписания",
            description = "Удаляет запись расписания мастера"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Запись расписания успешно удалена"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "❌ Запись расписания не найдена"
            )
    })
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(
            @Parameter(description = "ID записи расписания", example = "1", required = true)
            @PathVariable Long scheduleId) {
        masterScheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.ok().build();
    }
}