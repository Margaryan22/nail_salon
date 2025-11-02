package Frolov_back.NAILS_WEB_APP.controller;

import Frolov_back.NAILS_WEB_APP.service.AppointmentService;
import Frolov_back.NAILS_WEB_APP.service.DTO.AppointmentDto;
import Frolov_back.NAILS_WEB_APP.service.DTO.CreateAppointmentRequestDto;
import Frolov_back.NAILS_WEB_APP.service.DTO.TimeSlotDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "3. Система записи", description = "API для управления записями на услуги")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Operation(summary = "Создать новую запись")
    @PostMapping
    public ResponseEntity<AppointmentDto> createAppointment(@RequestBody CreateAppointmentRequestDto requestDto) {
        AppointmentDto appointment = appointmentService.createAppointment(requestDto);
        return ResponseEntity.ok(appointment);
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
    public ResponseEntity<List<TimeSlotDto>> getAvailableTimeSlots(
            @PathVariable Long masterId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        List<TimeSlotDto> timeSlots = appointmentService.getAvailableTimeSlots(masterId, date);
        return ResponseEntity.ok(timeSlots);
    }

    @Operation(summary = "Проверить доступность времени")
    @GetMapping("/availability")
    public ResponseEntity<Boolean> checkTimeSlotAvailability(
            @RequestParam Long masterId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        boolean available = appointmentService.isTimeSlotAvailable(masterId, startTime, endTime);
        return ResponseEntity.ok(available);
    }

    private Long getCurrentUserId(Authentication authentication) {
        // Реализация получения ID текущего пользователя
        return 1L; // Заглушка - нужно реализовать
    }
}
