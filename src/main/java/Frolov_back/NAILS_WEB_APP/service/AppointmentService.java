package Frolov_back.NAILS_WEB_APP.service;

import Frolov_back.NAILS_WEB_APP.service.DTO.AppointmentDto;
import Frolov_back.NAILS_WEB_APP.service.DTO.CreateAppointmentRequestDto;
import Frolov_back.NAILS_WEB_APP.service.DTO.TimeSlotDto;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {
    // Создание новой записи
    AppointmentDto createAppointment(CreateAppointmentRequestDto requestDto);

    // Подтверждение записи
    AppointmentDto confirmAppointment(Long appointmentId, Long masterId);

    // Отмена записи
    AppointmentDto cancelAppointment(Long appointmentId, String cancellationReason, boolean byClient);

    // Завершение записи
    AppointmentDto completeAppointment(Long appointmentId);

    // Поиск записей
    List<AppointmentDto> getClientAppointments(Long clientId);
    List<AppointmentDto> getMasterAppointments(Long masterId);
    List<AppointmentDto> getAppointmentsByDate(LocalDateTime date);

    // Проверка доступности времени
    boolean isTimeSlotAvailable(Long masterId, LocalDateTime startTime, LocalDateTime endTime);

    // Получение свободных слотов мастера
    List<TimeSlotDto> getAvailableTimeSlots(Long masterId, LocalDateTime date);
}