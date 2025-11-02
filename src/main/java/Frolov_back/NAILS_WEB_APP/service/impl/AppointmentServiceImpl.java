package Frolov_back.NAILS_WEB_APP.service.impl;

import Frolov_back.NAILS_WEB_APP.domain.*;
import Frolov_back.NAILS_WEB_APP.repository.*;
import Frolov_back.NAILS_WEB_APP.service.AppointmentService;
import Frolov_back.NAILS_WEB_APP.service.DTO.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final SystemUserRepository systemUserRepository;
    private final NailServiceRepository serviceRepository;
    private final MasterServiceEntityRepository masterServiceRepository;
    private final MasterScheduleTemplateRepository scheduleTemplateRepository;
    private final MasterTimeOffRepository timeOffRepository;

    @Override
    @Transactional
    public AppointmentDto createAppointment(CreateAppointmentRequestDto requestDto) {
        // Проверяем существование пользователей и услуги
        SystemUser client = systemUserRepository.findById(requestDto.getClientId())
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));

        SystemUser master = systemUserRepository.findById(requestDto.getMasterId())
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        NailService service = serviceRepository.findById(requestDto.getServiceId())
                .orElseThrow(() -> new RuntimeException("Услуга не найдена"));

        // Проверяем что мастер предоставляет эту услугу
        MasterServiceEntity masterService = masterServiceRepository
                .findByMasterAndService(master, service)
                .orElseThrow(() -> new RuntimeException("Мастер не предоставляет эту услугу"));

        // Рассчитываем время окончания
        LocalDateTime endDateTime = requestDto.getAppointmentDateTime()
                .plusMinutes(service.getBaseDuration());

        // Проверяем доступность времени
        if (!isTimeSlotAvailable(master.getUserId(), requestDto.getAppointmentDateTime(), endDateTime)) {
            throw new RuntimeException("Время занято или недоступно");
        }

        // Определяем цену (индивидуальная цена мастера или базовая)
        BigDecimal price = masterService.getMasterPrice() != null ?
                masterService.getMasterPrice() : service.getBasePrice();

        // Создаем запись
        Appointment appointment = new Appointment();
        appointment.setClient(client);
        appointment.setMaster(master);
        appointment.setService(service);
        appointment.setAppointmentDatetime(requestDto.getAppointmentDateTime());
        appointment.setEndDatetime(endDateTime);
        appointment.setPrice(price);
        appointment.setStatus(AppointmentStatusType.BOOKED);
        appointment.setNotes(requestDto.getNotes());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return convertToDto(savedAppointment);
    }

    @Override
    public boolean isTimeSlotAvailable(Long masterId, LocalDateTime startTime, LocalDateTime endTime) {
        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        // 1. Проверяем рабочие часы
        if (!isWithinWorkingHours(master, startTime, endTime)) {
            return false;
        }

        // 2. Проверяем выходные
        if (hasTimeOffConflict(master, startTime, endTime)) {
            return false;
        }

        // 3. Проверяем существующие записи
        return appointmentRepository.isTimeSlotAvailable(master, startTime, endTime);
    }

    private boolean isWithinWorkingHours(SystemUser master, LocalDateTime startTime, LocalDateTime endTime) {
        Short dayOfWeek = (short) startTime.getDayOfWeek().getValue();
        List<MasterScheduleTemplate> schedules = scheduleTemplateRepository
                .findByMasterAndDayOfWeek(master, dayOfWeek);

        if (schedules.isEmpty()) {
            return false; // Мастер не работает в этот день
        }

        return schedules.stream()
                .anyMatch(schedule -> {
                    LocalTime startLocal = startTime.toLocalTime();
                    LocalTime endLocal = endTime.toLocalTime();
                    return !startLocal.isBefore(schedule.getStartTime()) &&
                            !endLocal.isAfter(schedule.getEndTime());
                });
    }

    private boolean hasTimeOffConflict(SystemUser master, LocalDateTime startTime, LocalDateTime endTime) {
        return timeOffRepository.existsTimeOffConflict(master, startTime, endTime);
    }

    @Override
    public List<TimeSlotDto> getAvailableTimeSlots(Long masterId, LocalDateTime date) {
        List<TimeSlotDto> timeSlots = new ArrayList<>();
        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        Short dayOfWeek = (short) date.getDayOfWeek().getValue();
        List<MasterScheduleTemplate> schedules = scheduleTemplateRepository
                .findByMasterAndDayOfWeek(master, dayOfWeek);

        if (schedules.isEmpty()) {
            return timeSlots;
        }

        // Генерируем слоты по 30 минут в рабочие часы
        for (MasterScheduleTemplate schedule : schedules) {
            LocalDateTime currentSlot = date.with(schedule.getStartTime());
            LocalDateTime endOfDay = date.with(schedule.getEndTime());

            while (currentSlot.isBefore(endOfDay)) {
                LocalDateTime slotEnd = currentSlot.plusMinutes(30);

                boolean available = isTimeSlotAvailable(masterId, currentSlot, slotEnd);
                timeSlots.add(new TimeSlotDto(currentSlot, slotEnd, available));

                currentSlot = slotEnd;
            }
        }

        return timeSlots;
    }

    @Override
    @Transactional
    public AppointmentDto confirmAppointment(Long appointmentId, Long masterId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Запись не найдена"));

        // Проверяем что подтверждает именно мастер этой записи
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