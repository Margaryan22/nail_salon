package Frolov_back.NAILS_WEB_APP.impl.for_shedule_master;

import Frolov_back.NAILS_WEB_APP.DTO.for_shedule_master.MasterWeeklyScheduleDto;
import Frolov_back.NAILS_WEB_APP.DTO.for_shedule_master.WeeklyScheduleRequestDto;
import Frolov_back.NAILS_WEB_APP.domain.*;
import Frolov_back.NAILS_WEB_APP.domain.for_shedule_master.DayOfWeekEnum;
import Frolov_back.NAILS_WEB_APP.domain.for_shedule_master.MasterScheduleSlot;
import Frolov_back.NAILS_WEB_APP.domain.for_shedule_master.MasterWeeklySchedule;
import Frolov_back.NAILS_WEB_APP.repository.SystemUserRepository;
import Frolov_back.NAILS_WEB_APP.repository.for_shedule_master.MasterScheduleSlotRepository;
import Frolov_back.NAILS_WEB_APP.repository.for_shedule_master.MasterWeeklyScheduleRepository;
import Frolov_back.NAILS_WEB_APP.service.for_shedule_master.MasterScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MasterScheduleServiceImpl implements MasterScheduleService {

    private final MasterWeeklyScheduleRepository scheduleRepository;
    private final MasterScheduleSlotRepository slotRepository;
    private final SystemUserRepository systemUserRepository;

    @Override
    @Transactional
    public MasterWeeklyScheduleDto createOrUpdateWeeklySchedule(WeeklyScheduleRequestDto requestDto) {
        validateScheduleRequest(requestDto);

        SystemUser master = systemUserRepository.findById(requestDto.getMasterId())
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        LocalDate weekStartDate = getWeekStartDate(LocalDate.now());

        // Ищем существующее расписание или создаем новое
        MasterWeeklySchedule schedule = scheduleRepository
                .findByMasterAndWeekStartDate(master, weekStartDate)
                .orElse(new MasterWeeklySchedule());

        schedule.setMaster(master);
        schedule.setWeekStartDate(weekStartDate);

        // ОЧИЩАЕМ СТАРЫЕ СЛОТЫ И ДОБАВЛЯЕМ НОВЫЕ
        schedule.clearScheduleSlots();

        for (Map.Entry<Integer, Integer[]> entry : requestDto.getDailySlots().entrySet()) {
            String slotsString = Arrays.stream(entry.getValue())
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            schedule.addScheduleSlot(entry.getKey(), slotsString);
        }

        MasterWeeklySchedule savedSchedule = scheduleRepository.save(schedule);
        return convertToDto(savedSchedule);
    }

    @Override
    @Transactional(readOnly = true)
    public MasterWeeklyScheduleDto getWeeklySchedule(Long masterId, LocalDate weekStartDate) {
        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        MasterWeeklySchedule schedule = scheduleRepository
                .findByMasterAndWeekStartDate(master, weekStartDate)
                .orElseThrow(() -> new RuntimeException("Расписание не найдено"));

        return convertToDto(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public MasterWeeklyScheduleDto getCurrentWeeklySchedule(Long masterId) {
        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        LocalDate currentDate = LocalDate.now();
        LocalDate weekStart = getWeekStartDate(currentDate);

        MasterWeeklySchedule schedule = scheduleRepository
                .findByMasterAndWeekStartDate(master, weekStart)
                .orElseThrow(() -> new RuntimeException("Текущее расписание не найдено"));

        return convertToDto(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MasterWeeklyScheduleDto> getAllMasterSchedules(Long masterId) {
        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        return scheduleRepository.findByMaster(master).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteWeeklySchedule(Long masterId, LocalDate weekStartDate) {
        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        scheduleRepository.deleteByMasterAndWeekStartDate(master, weekStartDate);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTimeSlotAvailable(Long masterId, LocalDate date, Integer timeSlot) {
        validateTimeSlot(timeSlot);

        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        LocalDate weekStart = getWeekStartDate(date);

        Optional<MasterWeeklySchedule> scheduleOpt = scheduleRepository
                .findByMasterAndWeekStartDate(master, weekStart);

        if (scheduleOpt.isEmpty()) {
            return false;
        }

        MasterWeeklySchedule schedule = scheduleOpt.get();
        int dayOfWeek = date.getDayOfWeek().getValue();

        // Ищем слоты для этого дня
        Optional<MasterScheduleSlot> slotForDay = schedule.getScheduleSlots().stream()
                .filter(slot -> slot.getDayOfWeek().equals(dayOfWeek))
                .findFirst();

        if (slotForDay.isEmpty()) {
            return false;
        }

        List<Integer> availableSlots = convertStringToSlots(slotForDay.get().getAvailableSlots());
        return availableSlots.contains(timeSlot);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getAvailableSlotsForDay(Long masterId, LocalDate date) {
        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        LocalDate weekStart = getWeekStartDate(date);

        Optional<MasterWeeklySchedule> scheduleOpt = scheduleRepository
                .findByMasterAndWeekStartDate(master, weekStart);

        if (scheduleOpt.isEmpty()) {
            return List.of();
        }

        MasterWeeklySchedule schedule = scheduleOpt.get();
        int dayOfWeek = date.getDayOfWeek().getValue();

        // Ищем слоты для этого дня
        Optional<MasterScheduleSlot> slotForDay = schedule.getScheduleSlots().stream()
                .filter(slot -> slot.getDayOfWeek().equals(dayOfWeek))
                .findFirst();

        if (slotForDay.isEmpty()) {
            return List.of();
        }

        return convertStringToSlots(slotForDay.get().getAvailableSlots());
    }

    // ========== ВАЛИДАЦИЯ ==========
    private void validateScheduleRequest(WeeklyScheduleRequestDto requestDto) {
        if (requestDto.getMasterId() == null) {
            throw new RuntimeException("ID мастера обязателен");
        }

        if (requestDto.getDailySlots() == null || requestDto.getDailySlots().isEmpty()) {
            throw new RuntimeException("Расписание не может быть пустым");
        }

        for (Map.Entry<Integer, Integer[]> entry : requestDto.getDailySlots().entrySet()) {
            int day = entry.getKey();
            if (day < 1 || day > 7) {
                throw new RuntimeException("Некорректный день недели: " + day);
            }

            for (Integer slot : entry.getValue()) {
                validateTimeSlot(slot);
            }
        }
    }

    private void validateTimeSlot(Integer timeSlot) {
        if (timeSlot == null || timeSlot < 1 || timeSlot > 10) {
            throw new RuntimeException("Некорректный номер слота: " + timeSlot + ". Допустимые значения: 1-10");
        }
    }

    // ========== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==========
    private LocalDate getWeekStartDate(LocalDate date) {
        return date.with(DayOfWeek.MONDAY);
    }

    private List<Integer> convertStringToSlots(String slotsString) {
        if (slotsString == null || slotsString.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(slotsString.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    private MasterWeeklyScheduleDto convertToDto(MasterWeeklySchedule schedule) {
        MasterWeeklyScheduleDto dto = new MasterWeeklyScheduleDto();
        dto.setScheduleId(schedule.getScheduleId());
        dto.setMasterId(schedule.getMaster().getUserId());
        dto.setMasterName(schedule.getMaster().getFirstName() + " " + schedule.getMaster().getLastName());
        dto.setWeekStartDate(schedule.getWeekStartDate());
        dto.setCreatedAt(schedule.getCreatedAt());
        dto.setUpdatedAt(schedule.getUpdatedAt());

        // СОЗДАЕМ МАПУ ДЛЯ БЫСТРОГО ДОСТУПА К СЛОТАМ ПО ДНЯМ
        Map<Integer, String> slotsByDay = schedule.getScheduleSlots().stream()
                .collect(Collectors.toMap(
                        MasterScheduleSlot::getDayOfWeek,
                        MasterScheduleSlot::getAvailableSlots
                ));

        // КОНВЕРТИРУЕМ В УДОБНЫЙ ФОРМАТ ДЛЯ ФРОНТЕНДА
        Map<String, Integer[]> weeklySchedule = new LinkedHashMap<>();
        for (int day = 1; day <= 7; day++) {
            String dayName = DayOfWeekEnum.fromNumber(day).getRussianName();
            String availableSlotsStr = slotsByDay.get(day);

            if (availableSlotsStr != null) {
                Integer[] slots = convertStringToSlots(availableSlotsStr).toArray(new Integer[0]);
                weeklySchedule.put(dayName, slots);
            } else {
                weeklySchedule.put(dayName, new Integer[0]);
            }
        }
        dto.setWeeklySchedule(weeklySchedule);

        return dto;
    }
}