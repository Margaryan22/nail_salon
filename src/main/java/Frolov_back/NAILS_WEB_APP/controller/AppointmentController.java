package Frolov_back.NAILS_WEB_APP.controller;

import Frolov_back.NAILS_WEB_APP.domain.for_shedule_master.TimeSlotEnum;
import Frolov_back.NAILS_WEB_APP.service.appointment.AppointmentService;
import Frolov_back.NAILS_WEB_APP.DTO.AppointmentDto;
import Frolov_back.NAILS_WEB_APP.DTO.appointment.CreateAppointmentRequestDto;
import Frolov_back.NAILS_WEB_APP.DTO.appointment.TimeSlotDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "3. Система записи", description = "API для управления записями на услуги")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Operation(summary = "Создать новую запись")
    @PostMapping
    public ResponseEntity<?> createAppointment(@Valid @RequestBody CreateAppointmentRequestDto requestDto) {
        try {
            AppointmentDto appointment = appointmentService.createAppointment(requestDto);
            return ResponseEntity.ok(appointment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }


    @Operation(summary = "Подтвердить запись (для мастера)")
    @PostMapping("/{appointmentId}/confirm")
    public ResponseEntity<AppointmentDto> confirmAppointment(
            @PathVariable Long appointmentId,
            Authentication authentication) {
        // Получаем ID мастера из аутентификации
        Long masterId = getCurrentUserId(authentication);
        AppointmentDto appointment = appointmentService.confirmAppointment(appointmentId, masterId);
        return ResponseEntity.ok(appointment);
    }

    @Operation(summary = "Отменить запись")
    @PostMapping("/{appointmentId}/cancel")
    public ResponseEntity<AppointmentDto> cancelAppointment(
            @PathVariable Long appointmentId,
            @RequestParam String reason,
            @RequestParam(defaultValue = "true") boolean byClient) {
        AppointmentDto appointment = appointmentService.cancelAppointment(appointmentId, reason, byClient);
        return ResponseEntity.ok(appointment);
    }

    @Operation(summary = "Завершить запись")
    @PostMapping("/{appointmentId}/complete")
    public ResponseEntity<AppointmentDto> completeAppointment(@PathVariable Long appointmentId) {
        AppointmentDto appointment = appointmentService.completeAppointment(appointmentId);
        return ResponseEntity.ok(appointment);
    }

    @Operation(summary = "Получить записи клиента")
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<AppointmentDto>> getClientAppointments(@PathVariable Long clientId) {
        List<AppointmentDto> appointments = appointmentService.getClientAppointments(clientId);
        return ResponseEntity.ok(appointments);
    }

    @Operation(summary = "Получить записи мастера")
    @GetMapping("/master/{masterId}")
    public ResponseEntity<List<AppointmentDto>> getMasterAppointments(@PathVariable Long masterId) {
        List<AppointmentDto> appointments = appointmentService.getMasterAppointments(masterId);
        return ResponseEntity.ok(appointments);
    }

    @Operation(summary = "Получить записи на дату")
    @GetMapping("/date")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        List<AppointmentDto> appointments = appointmentService.getAppointmentsByDate(date);
        return ResponseEntity.ok(appointments);
    }

    @Operation(summary = "Получить доступные слоты времени мастера")
    @GetMapping("/master/{masterId}/available-slots")
    public ResponseEntity<?> getAvailableTimeSlots(
            @PathVariable Long masterId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<TimeSlotDto> timeSlots = appointmentService.getAvailableTimeSlots(masterId, date);
            return ResponseEntity.ok(timeSlots);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @Operation(summary = "Проверить доступность времени")
    @GetMapping("/availability")
    public ResponseEntity<Boolean> checkTimeSlotAvailability(
            @RequestParam Long masterId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Integer timeSlot) {
        // Используем новую систему для проверки
        boolean available = appointmentService.isTimeSlotAvailable(
                masterId,
                TimeSlotEnum.toStartDateTime(date, timeSlot),
                TimeSlotEnum.toEndDateTime(date, timeSlot)
        );
        return ResponseEntity.ok(available);
    }

    private Long getCurrentUserId(Authentication authentication) {
        // Реализация получения ID текущего пользователя
        return 1L; // Заглушка - нужно реализовать
    }
}
