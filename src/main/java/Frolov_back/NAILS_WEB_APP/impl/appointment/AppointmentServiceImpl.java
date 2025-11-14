package Frolov_back.NAILS_WEB_APP.impl.appointment;

import Frolov_back.NAILS_WEB_APP.DTO.AppointmentDto;
import Frolov_back.NAILS_WEB_APP.DTO.appointment.CreateAppointmentRequestDto;
import Frolov_back.NAILS_WEB_APP.DTO.appointment.TimeSlotDto;
import Frolov_back.NAILS_WEB_APP.domain.*;
import Frolov_back.NAILS_WEB_APP.domain.for_shedule_master.TimeSlotEnum;
import Frolov_back.NAILS_WEB_APP.repository.*;
import Frolov_back.NAILS_WEB_APP.service.appointment.AppointmentService;
import Frolov_back.NAILS_WEB_APP.service.for_shedule_master.MasterScheduleManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final SystemUserRepository systemUserRepository;
    private final NailServiceRepository serviceRepository;
    private final MasterServiceEntityRepository masterServiceRepository;
    private final MasterScheduleManagementService scheduleManagementService;
    private final MasterTimeOffRepository timeOffRepository;

    @Override
    @Transactional
    public AppointmentDto createAppointment(CreateAppointmentRequestDto requestDto) {
        // 1. ВАЛИДАЦИЯ ОСНОВНЫХ ДАННЫХ
        validateAppointmentRequest(requestDto);

        // 2. ПРОВЕРКА СУЩЕСТВОВАНИЯ СУЩНОСТЕЙ
        SystemUser client = systemUserRepository.findById(requestDto.getClientId())
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));

        SystemUser master = systemUserRepository.findById(requestDto.getMasterId())
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        NailService service = serviceRepository.findById(requestDto.getServiceId())
                .orElseThrow(() -> new RuntimeException("Услуга не найдена"));

        // 3. ПРОВЕРКА ЧТО МАСТЕР ПРЕДОСТАВЛЯЕТ УСЛУГУ
        MasterServiceEntity masterService = masterServiceRepository
                .findByMasterAndService(master, service)
                .orElseThrow(() -> new RuntimeException("Мастер не предоставляет эту услугу"));

        // 4. ✅ ПРОВЕРКА ДОСТУПНОСТИ СЛОТА ЧЕРЕЗ НОВУЮ СИСТЕМУ
        if (!scheduleManagementService.isTimeSlotAvailable(
                master.getUserId(),
                requestDto.getAppointmentDate(),
                requestDto.getTimeSlot())) {
            throw new RuntimeException("Выбранный временной слот недоступен в расписании мастера");
        }

        // 5. РАСЧЕТ ВРЕМЕНИ И ПРОВЕРКА КОНФЛИКТОВ
        LocalDateTime startDateTime = TimeSlotEnum.toStartDateTime(requestDto.getAppointmentDate(), requestDto.getTimeSlot());
        LocalDateTime endDateTime = calculateEndDateTime(startDateTime, service);

        // 6. ПРОВЕРКА НАЛОЖЕНИЯ С СУЩЕСТВУЮЩИМИ ЗАПИСЯМИ
        if (!isTimeSlotAvailable(master.getUserId(), startDateTime, endDateTime)) {
            throw new RuntimeException("Время занято другой записью");
        }

        // 7. ПРОВЕРКА ВЫХОДНЫХ МАСТЕРА
        if (hasTimeOffConflict(master, startDateTime, endDateTime)) {
            throw new RuntimeException("Мастер в это время отсутствует");
        }

        // 8. СОЗДАНИЕ ЗАПИСИ
        Appointment appointment = buildAppointment(client, master, service, masterService, startDateTime, endDateTime, requestDto.getNotes());
        Appointment savedAppointment = appointmentRepository.save(appointment);

        return convertToDto(savedAppointment);
    }

    @Override
    public List<TimeSlotDto> getAvailableTimeSlots(Long masterId, LocalDate date) {
        validateDate(date);

        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        // ✅ ПОЛУЧАЕМ ДОСТУПНЫЕ СЛОТЫ ИЗ НОВОЙ СИСТЕМЫ
        List<Integer> availableSlotNumbers = scheduleManagementService.getAvailableSlotsForDay(masterId, date);

        List<TimeSlotDto> timeSlots = new ArrayList<>();

        // ФИЛЬТРУЕМ СЛОТЫ С УЧЕТОМ СУЩЕСТВУЮЩИХ ЗАПИСЕЙ
        for (Integer slotNumber : availableSlotNumbers) {
            LocalDateTime startTime = TimeSlotEnum.toStartDateTime(date, slotNumber);
            LocalDateTime endTime = TimeSlotEnum.toEndDateTime(date, slotNumber);

            // ДОПОЛНИТЕЛЬНО ПРОВЕРЯЕМ НЕТ ЛИ ЗАПИСЕЙ В ЭТО ВРЕМЯ
            boolean hasAppointmentConflict = !appointmentRepository.isTimeSlotAvailable(master, startTime, endTime);
            boolean hasTimeOffConflict = timeOffRepository.existsTimeOffConflict(master, startTime, endTime);

            // СЛOT ДОСТУПЕН ТОЛЬКО ЕСЛИ:
            // - ЕСТЬ В РАСПИСАНИИ
            // - НЕТ КОНФЛИКТУЮЩИХ ЗАПИСЕЙ
            // - НЕТ ВЫХОДНЫХ
            boolean available = !hasAppointmentConflict && !hasTimeOffConflict;

            timeSlots.add(new TimeSlotDto(startTime, endTime, available, slotNumber));
        }

        return timeSlots;
    }

    @Override
    public boolean isTimeSlotAvailable(Long masterId, LocalDateTime startTime, LocalDateTime endTime) {
        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        // ПРОВЕРЯЕМ КОНФЛИКТЫ С СУЩЕСТВУЮЩИМИ ЗАПИСЯМИ
        boolean noAppointmentConflict = appointmentRepository.isTimeSlotAvailable(master, startTime, endTime);

        // ПРОВЕРЯЕМ ВЫХОДНЫЕ
        boolean noTimeOffConflict = !timeOffRepository.existsTimeOffConflict(master, startTime, endTime);

        return noAppointmentConflict && noTimeOffConflict;
    }

    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===

    private void validateAppointmentRequest(CreateAppointmentRequestDto requestDto) {
        if (requestDto.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Дата записи не может быть в прошлом");
        }

        if (requestDto.getTimeSlot() < 1 || requestDto.getTimeSlot() > 10) {
            throw new RuntimeException("Некорректный номер слота. Допустимые значения: 1-10");
        }
    }

    private void validateDate(LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new RuntimeException("Нельзя получить слоты для прошедшей даты");
        }
    }

    private LocalDateTime calculateEndDateTime(LocalDateTime startDateTime, NailService service) {
        return startDateTime.plusMinutes(service.getBaseDuration());
    }

    private boolean hasTimeOffConflict(SystemUser master, LocalDateTime startTime, LocalDateTime endTime) {
        return timeOffRepository.existsTimeOffConflict(master, startTime, endTime);
    }

    private Appointment buildAppointment(SystemUser client, SystemUser master, NailService service,
                                         MasterServiceEntity masterService, LocalDateTime startTime,
                                         LocalDateTime endTime, String notes) {
        Appointment appointment = new Appointment();
        appointment.setClient(client);
        appointment.setMaster(master);
        appointment.setService(service);
        appointment.setAppointmentDatetime(startTime);
        appointment.setEndDatetime(endTime);

        // ИСПОЛЬЗУЕМ ИНДИВИДУАЛЬНУЮ ЦЕНУ МАСТЕРА ИЛИ БАЗОВУЮ
        BigDecimal price = masterService.getMasterPrice() != null ?
                masterService.getMasterPrice() : service.getBasePrice();
        appointment.setPrice(price);

        appointment.setStatus(AppointmentStatusType.BOOKED);
        appointment.setNotes(notes);

        return appointment;
    }

    // Остальные методы остаются без изменений
    @Override
    @Transactional
    public AppointmentDto confirmAppointment(Long appointmentId, Long masterId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Запись не найдена"));

        if (!appointment.getMaster().getUserId().equals(masterId)) {
            throw new RuntimeException("Недостаточно прав для подтверждения записи");
        }

        appointment.setStatus(AppointmentStatusType.CONFIRMED);
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return convertToDto(updatedAppointment);
    }

    @Override
    @Transactional
    public AppointmentDto cancelAppointment(Long appointmentId, String cancellationReason, boolean byClient) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Запись не найдена"));

        AppointmentStatusType newStatus = byClient ?
                AppointmentStatusType.CANCELLED_BY_CLIENT :
                AppointmentStatusType.CANCELLED_BY_SALON;

        appointment.setStatus(newStatus);
        appointment.setNotes(appointment.getNotes() + "\nПричина отмены: " + cancellationReason);

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return convertToDto(updatedAppointment);
    }

    @Override
    @Transactional
    public AppointmentDto completeAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Запись не найдена"));

        appointment.setStatus(AppointmentStatusType.COMPLETED);
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return convertToDto(updatedAppointment);
    }

    @Override
    public List<AppointmentDto> getClientAppointments(Long clientId) {
        SystemUser client = systemUserRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));

        return appointmentRepository.findByClient(client).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<AppointmentDto> getMasterAppointments(Long masterId) {
        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        return appointmentRepository.findByMaster(master).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<AppointmentDto> getAppointmentsByDate(LocalDateTime date) {
        LocalDateTime startOfDay = date.with(LocalTime.MIN);
        LocalDateTime endOfDay = date.with(LocalTime.MAX);

        return appointmentRepository.findByAppointmentDatetimeBetween(startOfDay, endOfDay).stream()
                .map(this::convertToDto)
                .toList();
    }

    private AppointmentDto convertToDto(Appointment appointment) {
        AppointmentDto dto = new AppointmentDto();
        dto.setAppointmentId(appointment.getAppointmentId());
        dto.setClientId(appointment.getClient().getUserId());
        dto.setClientName(appointment.getClient().getFirstName() + " " + appointment.getClient().getLastName());
        dto.setMasterId(appointment.getMaster().getUserId());
        dto.setMasterName(appointment.getMaster().getFirstName() + " " + appointment.getMaster().getLastName());
        dto.setServiceId(appointment.getService().getServiceId());
        dto.setServiceName(appointment.getService().getName());
        dto.setAppointmentDatetime(appointment.getAppointmentDatetime());
        dto.setEndDatetime(appointment.getEndDatetime());
        dto.setPrice(appointment.getPrice());
        dto.setStatus(appointment.getStatus().name());
        dto.setNotes(appointment.getNotes());
        dto.setCreatedAt(appointment.getCreatedAt());

        return dto;
    }
}