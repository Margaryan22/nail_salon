package Frolov_back.NAILS_WEB_APP.impl.appointment;

import Frolov_back.NAILS_WEB_APP.DTO.AppointmentDto;
import Frolov_back.NAILS_WEB_APP.DTO.appointment.CreateAppointmentRequestDto;
import Frolov_back.NAILS_WEB_APP.DTO.appointment.TimeSlotDto;
import Frolov_back.NAILS_WEB_APP.domain.*;
import Frolov_back.NAILS_WEB_APP.domain.for_shedule_master.TimeSlotEnum;
import Frolov_back.NAILS_WEB_APP.repository.*;
import Frolov_back.NAILS_WEB_APP.service.appointment.AppointmentService;
import Frolov_back.NAILS_WEB_APP.service.for_shedule_master.MasterScheduleService;
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
    private final MasterScheduleService masterScheduleService; // ДОБАВЛЯЕМ
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

        // ПРОВЕРЯЕМ ДОСТУПНОСТЬ СЛОТА через новую систему
        if (!masterScheduleService.isTimeSlotAvailable(
                master.getUserId(),
                requestDto.getAppointmentDate(),
                requestDto.getTimeSlot())) {
            throw new RuntimeException("Выбранный временной слот недоступен");
        }

        // ПРОВЕРЯЕМ НАЛОЖЕНИЯ С СУЩЕСТВУЮЩИМИ ЗАПИСЯМИ
        LocalDateTime startDateTime = TimeSlotEnum.toStartDateTime(requestDto.getAppointmentDate(), requestDto.getTimeSlot());
        LocalDateTime endDateTime = TimeSlotEnum.toEndDateTime(requestDto.getAppointmentDate(), requestDto.getTimeSlot());

        if (!isTimeSlotAvailable(master.getUserId(), startDateTime, endDateTime)) {
            throw new RuntimeException("Время занято другой записью");
        }

        // Определяем цену (индивидуальная цена мастера или базовая)
        BigDecimal price = masterService.getMasterPrice() != null ?
                masterService.getMasterPrice() : service.getBasePrice();

        // Создаем запись
        Appointment appointment = new Appointment();
        appointment.setClient(client);
        appointment.setMaster(master);
        appointment.setService(service);
        appointment.setAppointmentDatetime(startDateTime);
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

        // 1. Проверяем выходные
        if (hasTimeOffConflict(master, startTime, endTime)) {
            return false;
        }

        // 2. Проверяем существующие записи (старая логика для проверки наложений)
        return appointmentRepository.isTimeSlotAvailable(master, startTime, endTime);
    }

    @Override
    public List<TimeSlotDto> getAvailableTimeSlots(Long masterId, LocalDate date) {
        List<TimeSlotDto> timeSlots = new ArrayList<>();

        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        // Получаем доступные слоты из новой системы
        List<Integer> availableSlotNumbers = masterScheduleService.getAvailableSlotsForDay(masterId, date);

        // Конвертируем в TimeSlotDto
        for (Integer slotNumber : availableSlotNumbers) {
            LocalDateTime startTime = TimeSlotEnum.toStartDateTime(date, slotNumber);
            LocalDateTime endTime = TimeSlotEnum.toEndDateTime(date, slotNumber);

            // Дополнительно проверяем нет ли записей в это время
            boolean hasAppointmentConflict = !appointmentRepository.isTimeSlotAvailable(
                    master,
                    startTime,
                    endTime
            );

            // Слот доступен только если он в расписании И нет конфликтующих записей
            boolean available = !hasAppointmentConflict;

            timeSlots.add(new TimeSlotDto(startTime, endTime, available, slotNumber));
        }

        return timeSlots;
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

    // ========== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==========

    private boolean hasTimeOffConflict(SystemUser master, LocalDateTime startTime, LocalDateTime endTime) {
        return timeOffRepository.existsTimeOffConflict(master, startTime, endTime);
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