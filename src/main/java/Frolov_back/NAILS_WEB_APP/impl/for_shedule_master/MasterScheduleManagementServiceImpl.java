package Frolov_back.NAILS_WEB_APP.impl.for_shedule_master;

import Frolov_back.NAILS_WEB_APP.DTO.for_shedule_master.*;
import Frolov_back.NAILS_WEB_APP.DTO.for_shedule_master.month.MonthlyScheduleDto;
import Frolov_back.NAILS_WEB_APP.DTO.for_shedule_master.month.MonthlyScheduleRequestDto;
import Frolov_back.NAILS_WEB_APP.domain.*;
import Frolov_back.NAILS_WEB_APP.domain.for_shedule_master.AdjustmentSlot;
import Frolov_back.NAILS_WEB_APP.domain.for_shedule_master.DailyScheduleException;
import Frolov_back.NAILS_WEB_APP.domain.for_shedule_master.DayOfWeekEnum;
import Frolov_back.NAILS_WEB_APP.domain.for_shedule_master.WeeklyScheduleAdjustment;
import Frolov_back.NAILS_WEB_APP.domain.for_shedule_master.month.MasterMonthlySchedule;
import Frolov_back.NAILS_WEB_APP.domain.for_shedule_master.month.MonthlyScheduleSlot;
import Frolov_back.NAILS_WEB_APP.repository.*;
import Frolov_back.NAILS_WEB_APP.repository.for_shedule_master.DailyScheduleExceptionRepository;
import Frolov_back.NAILS_WEB_APP.repository.for_shedule_master.WeeklyScheduleAdjustmentRepository;
import Frolov_back.NAILS_WEB_APP.repository.for_shedule_master.month.MasterMonthlyScheduleRepository;
import Frolov_back.NAILS_WEB_APP.service.for_shedule_master.MasterScheduleManagementService;
import Frolov_back.NAILS_WEB_APP.DTO.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MasterScheduleManagementServiceImpl implements MasterScheduleManagementService {

    private final MasterMonthlyScheduleRepository monthlyScheduleRepository;
    private final WeeklyScheduleAdjustmentRepository adjustmentRepository;
    private final DailyScheduleExceptionRepository exceptionRepository;
    private final SystemUserRepository systemUserRepository;

    // === МЕСЯЧНОЕ РАСПИСАНИЕ ===
    @Override
    @Transactional
    public MonthlyScheduleDto createMonthlySchedule(MonthlyScheduleRequestDto requestDto) {
        validateMonthlyRequest(requestDto);

        SystemUser master = systemUserRepository.findById(requestDto.getMasterId())
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        // Проверяем не существует ли уже расписание на этот месяц
        if (monthlyScheduleRepository.existsByMasterAndScheduleMonth(master, requestDto.getMonth())) {
            throw new RuntimeException("Расписание на месяц " + requestDto.getMonth() + " уже существует");
        }

        MasterMonthlySchedule schedule = new MasterMonthlySchedule();
        schedule.setMaster(master);
        schedule.setScheduleMonth(requestDto.getMonth());
        schedule.setTemplateType(requestDto.getTemplateType());

        // Добавляем слоты
        for (Map.Entry<Integer, Integer[]> entry : requestDto.getDailySlots().entrySet()) {
            String slotsString = convertSlotsToString(entry.getValue());
            schedule.addMonthlySlot(entry.getKey(), slotsString);
        }

        MasterMonthlySchedule savedSchedule = monthlyScheduleRepository.save(schedule);
        return convertToMonthlyDto(savedSchedule);
    }

    @Override
    @Transactional(readOnly = true)
    public MonthlyScheduleDto getMonthlySchedule(Long masterId, YearMonth month) {
        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        MasterMonthlySchedule schedule = monthlyScheduleRepository
                .findByMasterAndScheduleMonth(master, month)
                .orElseThrow(() -> new RuntimeException("Месячное расписание не найдено"));

        return convertToMonthlyDto(schedule);
    }

    @Override
    @Transactional
    public void deleteMonthlySchedule(Long masterId, YearMonth month) {
        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        monthlyScheduleRepository.deleteByMasterAndScheduleMonth(master, month);
    }

    // === ОСНОВНАЯ ЛОГИКА ПРОВЕРКИ ДОСТУПНОСТИ ===
    @Override
    @Transactional(readOnly = true)
    public boolean isTimeSlotAvailable(Long masterId, LocalDate date, Integer timeSlot) {
        validateTimeSlot(timeSlot);

        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        // 1. ПРОВЕРКА ДНЕВНЫХ ИСКЛЮЧЕНИЙ (ВЫСШИЙ ПРИОРИТЕТ)
        Optional<DailyScheduleException> dailyException = exceptionRepository
                .findExceptionForDate(master, date);

        if (dailyException.isPresent()) {
            return checkAvailabilityInException(dailyException.get(), timeSlot);
        }

        // 2. ПРОВЕРКА НЕДЕЛЬНЫХ КОРРЕКТИРОВОК
        LocalDate weekStart = getWeekStartDate(date);
        Optional<WeeklyScheduleAdjustment> weeklyAdjustment = adjustmentRepository
                .findByMasterAndWeekStartDate(master, weekStart);

        if (weeklyAdjustment.isPresent()) {
            return checkAvailabilityInAdjustment(weeklyAdjustment.get(), date, timeSlot);
        }

        // 3. ПРОВЕРКА МЕСЯЧНОГО РАСПИСАНИЯ (БАЗОВЫЙ УРОВЕНЬ)
        YearMonth month = YearMonth.from(date);
        Optional<MasterMonthlySchedule> monthlySchedule = monthlyScheduleRepository
                .findByMasterAndScheduleMonth(master, month);

        if (monthlySchedule.isPresent()) {
            return checkAvailabilityInMonthlySchedule(monthlySchedule.get(), date, timeSlot);
        }

        return false; // Нет расписания вообще
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getAvailableSlotsForDay(Long masterId, LocalDate date) {
        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        // Аналогичная логика приоритетов
        Optional<DailyScheduleException> dailyException = exceptionRepository
                .findExceptionForDate(master, date);

        if (dailyException.isPresent()) {
            return getSlotsFromException(dailyException.get());
        }

        LocalDate weekStart = getWeekStartDate(date);
        Optional<WeeklyScheduleAdjustment> weeklyAdjustment = adjustmentRepository
                .findByMasterAndWeekStartDate(master, weekStart);

        if (weeklyAdjustment.isPresent()) {
            return getSlotsFromAdjustment(weeklyAdjustment.get(), date);
        }

        YearMonth month = YearMonth.from(date);
        Optional<MasterMonthlySchedule> monthlySchedule = monthlyScheduleRepository
                .findByMasterAndScheduleMonth(master, month);

        if (monthlySchedule.isPresent()) {
            return getSlotsFromMonthlySchedule(monthlySchedule.get(), date);
        }

        return List.of();
    }

    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ДЛЯ ПРОВЕРКИ ДОСТУПНОСТИ ===
    private boolean checkAvailabilityInException(DailyScheduleException exception, Integer timeSlot) {
        if (exception.getAllDay()) {
            return false; // Весь день выходной
        }
        // Если не весь день, проверяем доступные слоты
        List<Integer> availableSlots = convertStringToSlots(exception.getAvailableSlots());
        return availableSlots.contains(timeSlot);
    }

    private boolean checkAvailabilityInAdjustment(WeeklyScheduleAdjustment adjustment, LocalDate date, Integer timeSlot) {
        int dayOfWeek = date.getDayOfWeek().getValue();

        Optional<AdjustmentSlot> slotForDay = adjustment.getAdjustmentSlots().stream()
                .filter(slot -> slot.getDayOfWeek().equals(dayOfWeek))
                .findFirst();

        if (slotForDay.isEmpty()) {
            return false; // Нет корректировки на этот день
        }

        List<Integer> availableSlots = convertStringToSlots(slotForDay.get().getAvailableSlots());
        return availableSlots.contains(timeSlot);
    }

    private boolean checkAvailabilityInMonthlySchedule(MasterMonthlySchedule schedule, LocalDate date, Integer timeSlot) {
        int dayOfWeek = date.getDayOfWeek().getValue();

        Optional<MonthlyScheduleSlot> slotForDay = schedule.getMonthlySlots().stream()
                .filter(slot -> slot.getDayOfWeek().equals(dayOfWeek))
                .findFirst();

        if (slotForDay.isEmpty()) {
            return false;
        }

        List<Integer> availableSlots = convertStringToSlots(slotForDay.get().getAvailableSlots());
        return availableSlots.contains(timeSlot);
    }

    // Аналогичные методы для получения списка слотов...
    private List<Integer> getSlotsFromException(DailyScheduleException exception) {
        if (exception.getAllDay()) {
            return List.of();
        }
        return convertStringToSlots(exception.getAvailableSlots());
    }

    private List<Integer> getSlotsFromAdjustment(WeeklyScheduleAdjustment adjustment, LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();

        return adjustment.getAdjustmentSlots().stream()
                .filter(slot -> slot.getDayOfWeek().equals(dayOfWeek))
                .findFirst()
                .map(slot -> convertStringToSlots(slot.getAvailableSlots()))
                .orElse(List.of());
    }

    private List<Integer> getSlotsFromMonthlySchedule(MasterMonthlySchedule schedule, LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();

        return schedule.getMonthlySlots().stream()
                .filter(slot -> slot.getDayOfWeek().equals(dayOfWeek))
                .findFirst()
                .map(slot -> convertStringToSlots(slot.getAvailableSlots()))
                .orElse(List.of());
    }

    // === ВАЛИДАЦИЯ И ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===
    private void validateMonthlyRequest(MonthlyScheduleRequestDto requestDto) {
        if (requestDto.getMasterId() == null) {
            throw new RuntimeException("ID мастера обязателен");
        }
        if (requestDto.getMonth() == null) {
            throw new RuntimeException("Месяц обязателен");
        }
        if (requestDto.getDailySlots() == null || requestDto.getDailySlots().isEmpty()) {
            throw new RuntimeException("Расписание не может быть пустым");
        }
        validateDailySlots(requestDto.getDailySlots());
    }

    private void validateDailySlots(Map<Integer, Integer[]> dailySlots) {
        for (Map.Entry<Integer, Integer[]> entry : dailySlots.entrySet()) {
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

    private LocalDate getWeekStartDate(LocalDate date) {
        return date.with(DayOfWeek.MONDAY);
    }

    private String convertSlotsToString(Integer[] slots) {
        return Arrays.stream(slots)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
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

    private MonthlyScheduleDto convertToMonthlyDto(MasterMonthlySchedule schedule) {
        MonthlyScheduleDto dto = new MonthlyScheduleDto();
        dto.setMonthlyScheduleId(schedule.getMonthlyScheduleId());
        dto.setMasterId(schedule.getMaster().getUserId());
        dto.setMasterName(schedule.getMaster().getFirstName() + " " + schedule.getMaster().getLastName());
        dto.setMonth(schedule.getScheduleMonth());
        dto.setTemplateType(schedule.getTemplateType());
        dto.setCreatedAt(schedule.getCreatedAt());
        dto.setUpdatedAt(schedule.getUpdatedAt());

        // Конвертируем слоты в удобный формат
        Map<String, Integer[]> monthlySchedule = new LinkedHashMap<>();
        for (int day = 1; day <= 7; day++) {
            String dayName = DayOfWeekEnum.fromNumber(day).getRussianName();

            int finalDay = day;
            Optional<MonthlyScheduleSlot> slotForDay = schedule.getMonthlySlots().stream()
                    .filter(slot -> slot.getDayOfWeek().equals(finalDay))
                    .findFirst();

            if (slotForDay.isPresent()) {
                Integer[] slots = convertStringToSlots(slotForDay.get().getAvailableSlots()).toArray(new Integer[0]);
                monthlySchedule.put(dayName, slots);
            } else {
                monthlySchedule.put(dayName, new Integer[0]);
            }
        }
        dto.setMonthlySchedule(monthlySchedule);

        return dto;
    }

    @Override
    @Transactional
    public WeeklyAdjustmentDto createWeeklyAdjustment(WeeklyAdjustmentRequestDto requestDto) {
        validateWeeklyAdjustmentRequest(requestDto);

        SystemUser master = systemUserRepository.findById(requestDto.getMasterId())
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        // Проверяем не существует ли уже корректировка на эту неделю
        if (adjustmentRepository.existsByMasterAndWeekStartDate(master, requestDto.getWeekStartDate())) {
            throw new RuntimeException("Корректировка на неделю " + requestDto.getWeekStartDate() + " уже существует");
        }

        WeeklyScheduleAdjustment adjustment = new WeeklyScheduleAdjustment();
        adjustment.setMaster(master);
        adjustment.setWeekStartDate(requestDto.getWeekStartDate());
        adjustment.setReason(requestDto.getReason());

        // Добавляем корректировки слотов
        for (Map.Entry<Integer, Integer[]> entry : requestDto.getDailySlots().entrySet()) {
            String slotsString = convertSlotsToString(entry.getValue());
            adjustment.addAdjustmentSlot(entry.getKey(), slotsString);
        }

        WeeklyScheduleAdjustment savedAdjustment = adjustmentRepository.save(adjustment);
        return convertToWeeklyAdjustmentDto(savedAdjustment);
    }

    @Override
    @Transactional(readOnly = true)
    public WeeklyAdjustmentDto getWeeklyAdjustment(Long masterId, LocalDate weekStartDate) {
        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        WeeklyScheduleAdjustment adjustment = adjustmentRepository
                .findByMasterAndWeekStartDate(master, weekStartDate)
                .orElseThrow(() -> new RuntimeException("Корректировка не найдена"));

        return convertToWeeklyAdjustmentDto(adjustment);
    }

    @Override
    @Transactional
    public void deleteWeeklyAdjustment(Long masterId, LocalDate weekStartDate) {
        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        adjustmentRepository.findByMasterAndWeekStartDate(master, weekStartDate)
                .ifPresent(adjustmentRepository::delete);
    }

    @Override
    @Transactional
    public DailyExceptionDto createDailyException(DailyExceptionRequestDto requestDto) {
        validateDailyExceptionRequest(requestDto);

        SystemUser master = systemUserRepository.findById(requestDto.getMasterId())
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        // Проверяем не существует ли уже исключение на эту дату
        if (exceptionRepository.existsByMasterAndExceptionDate(master, requestDto.getExceptionDate())) {
            throw new RuntimeException("Исключение на дату " + requestDto.getExceptionDate() + " уже существует");
        }

        DailyScheduleException exception = new DailyScheduleException();
        exception.setMaster(master);
        exception.setExceptionDate(requestDto.getExceptionDate());
        exception.setExceptionType(requestDto.getExceptionType());
        exception.setAllDay(requestDto.getAllDay());
        exception.setReason(requestDto.getReason());

        // Если не весь день выходной, сохраняем доступные слоты
        if (!requestDto.getAllDay() && requestDto.getAvailableSlots() != null) {
            String slotsString = convertSlotsToString(requestDto.getAvailableSlots());
            exception.setAvailableSlots(slotsString);
        }

        DailyScheduleException savedException = exceptionRepository.save(exception);
        return convertToDailyExceptionDto(savedException);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyExceptionDto> getMonthlyExceptions(Long masterId, YearMonth month) {
        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        List<DailyScheduleException> exceptions = exceptionRepository
                .findByMasterAndExceptionDateBetween(master, startDate, endDate);

        return exceptions.stream()
                .map(this::convertToDailyExceptionDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteDailyException(Long exceptionId) {
        exceptionRepository.deleteById(exceptionId);
    }

    @Override
    @Transactional
    public List<MonthlyScheduleDto> createBulkMonthlySchedules(BulkScheduleRequestDto requestDto) {
        validateBulkScheduleRequest(requestDto);

        SystemUser master = systemUserRepository.findById(requestDto.getMasterId())
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        List<MonthlyScheduleDto> results = new ArrayList<>();

        for (YearMonth month : requestDto.getMonths()) {
            try {
                // Пропускаем месяцы, для которых уже есть расписание
                if (monthlyScheduleRepository.existsByMasterAndScheduleMonth(master, month)) {
                    continue;
                }

                MasterMonthlySchedule schedule = new MasterMonthlySchedule();
                schedule.setMaster(master);
                schedule.setScheduleMonth(month);
                schedule.setTemplateType(requestDto.getTemplateType());

                // Добавляем слоты из шаблона
                for (Map.Entry<Integer, Integer[]> entry : requestDto.getDailySlots().entrySet()) {
                    String slotsString = convertSlotsToString(entry.getValue());
                    schedule.addMonthlySlot(entry.getKey(), slotsString);
                }

                MasterMonthlySchedule savedSchedule = monthlyScheduleRepository.save(schedule);
                results.add(convertToMonthlyDto(savedSchedule));

            } catch (Exception e) {
                // Логируем ошибку, но продолжаем для остальных месяцев
                System.err.println("Ошибка создания расписания на " + month + ": " + e.getMessage());
            }
        }

        return results;
    }

    @Override
    @Transactional
    public MonthlyScheduleDto copyMonthSchedule(Long masterId, YearMonth sourceMonth, YearMonth targetMonth) {
        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        // Получаем исходное расписание
        MasterMonthlySchedule sourceSchedule = monthlyScheduleRepository
                .findByMasterAndScheduleMonth(master, sourceMonth)
                .orElseThrow(() -> new RuntimeException("Исходное расписание не найдено"));

        // Проверяем не существует ли уже расписание на целевой месяц
        if (monthlyScheduleRepository.existsByMasterAndScheduleMonth(master, targetMonth)) {
            throw new RuntimeException("Расписание на месяц " + targetMonth + " уже существует");
        }

        // Создаем новое расписание
        MasterMonthlySchedule targetSchedule = new MasterMonthlySchedule();
        targetSchedule.setMaster(master);
        targetSchedule.setScheduleMonth(targetMonth);
        targetSchedule.setTemplateType(sourceSchedule.getTemplateType());

        // Копируем слоты
        for (MonthlyScheduleSlot sourceSlot : sourceSchedule.getMonthlySlots()) {
            targetSchedule.addMonthlySlot(sourceSlot.getDayOfWeek(), sourceSlot.getAvailableSlots());
        }

        MasterMonthlySchedule savedSchedule = monthlyScheduleRepository.save(targetSchedule);
        return convertToMonthlyDto(savedSchedule);
    }

    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ДЛЯ КОНВЕРТАЦИИ ===
    private WeeklyAdjustmentDto convertToWeeklyAdjustmentDto(WeeklyScheduleAdjustment adjustment) {
        WeeklyAdjustmentDto dto = new WeeklyAdjustmentDto();
        dto.setAdjustmentId(adjustment.getAdjustmentId());
        dto.setMasterId(adjustment.getMaster().getUserId());
        dto.setMasterName(adjustment.getMaster().getFirstName() + " " + adjustment.getMaster().getLastName());
        dto.setWeekStartDate(adjustment.getWeekStartDate());
        dto.setReason(adjustment.getReason());
        dto.setCreatedAt(adjustment.getCreatedAt());

        // Конвертируем слоты корректировки
        Map<String, Integer[]> adjustedSchedule = new LinkedHashMap<>();
        for (int day = 1; day <= 7; day++) {
            String dayName = DayOfWeekEnum.fromNumber(day).getRussianName();

            int finalDay = day;
            Optional<AdjustmentSlot> slotForDay = adjustment.getAdjustmentSlots().stream()
                    .filter(slot -> slot.getDayOfWeek().equals(finalDay))
                    .findFirst();

            if (slotForDay.isPresent()) {
                Integer[] slots = convertStringToSlots(slotForDay.get().getAvailableSlots()).toArray(new Integer[0]);
                adjustedSchedule.put(dayName, slots);
            } else {
                adjustedSchedule.put(dayName, new Integer[0]);
            }
        }
        dto.setAdjustedSchedule(adjustedSchedule);

        return dto;
    }

    private DailyExceptionDto convertToDailyExceptionDto(DailyScheduleException exception) {
        DailyExceptionDto dto = new DailyExceptionDto();
        dto.setExceptionId(exception.getExceptionId());
        dto.setMasterId(exception.getMaster().getUserId());
        dto.setMasterName(exception.getMaster().getFirstName() + " " + exception.getMaster().getLastName());
        dto.setExceptionDate(exception.getExceptionDate());
        dto.setExceptionType(exception.getExceptionType());
        dto.setAllDay(exception.getAllDay());
        dto.setReason(exception.getReason());
        dto.setCreatedAt(exception.getCreatedAt());

        if (!exception.getAllDay() && exception.getAvailableSlots() != null) {
            Integer[] slots = convertStringToSlots(exception.getAvailableSlots()).toArray(new Integer[0]);
            dto.setAvailableSlots(slots);
        }

        return dto;
    }

    // === ДОПОЛНИТЕЛЬНАЯ ВАЛИДАЦИЯ ===
    private void validateWeeklyAdjustmentRequest(WeeklyAdjustmentRequestDto requestDto) {
        if (requestDto.getMasterId() == null) {
            throw new RuntimeException("ID мастера обязателен");
        }
        if (requestDto.getWeekStartDate() == null) {
            throw new RuntimeException("Дата начала недели обязательна");
        }
        validateDailySlots(requestDto.getDailySlots());
    }

    private void validateDailyExceptionRequest(DailyExceptionRequestDto requestDto) {
        if (requestDto.getMasterId() == null) {
            throw new RuntimeException("ID мастера обязателен");
        }
        if (requestDto.getExceptionDate() == null) {
            throw new RuntimeException("Дата исключения обязательна");
        }
        if (requestDto.getExceptionType() == null) {
            throw new RuntimeException("Тип исключения обязателен");
        }
        if (!requestDto.getAllDay() && (requestDto.getAvailableSlots() == null || requestDto.getAvailableSlots().length == 0)) {
            throw new RuntimeException("Для частичного исключения необходимо указать доступные слоты");
        }
    }

    private void validateBulkScheduleRequest(BulkScheduleRequestDto requestDto) {
        if (requestDto.getMasterId() == null) {
            throw new RuntimeException("ID мастера обязателен");
        }
        if (requestDto.getMonths() == null || requestDto.getMonths().isEmpty()) {
            throw new RuntimeException("Список месяцев обязателен");
        }
        if (requestDto.getTemplateType() == null) {
            throw new RuntimeException("Тип шаблона обязателен");
        }
        validateDailySlots(requestDto.getDailySlots());
    }
}