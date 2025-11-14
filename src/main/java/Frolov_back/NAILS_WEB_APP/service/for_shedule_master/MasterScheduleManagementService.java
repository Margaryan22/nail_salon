package Frolov_back.NAILS_WEB_APP.service.for_shedule_master;

import Frolov_back.NAILS_WEB_APP.DTO.for_shedule_master.*;
import Frolov_back.NAILS_WEB_APP.DTO.for_shedule_master.month.MonthlyScheduleDto;
import Frolov_back.NAILS_WEB_APP.DTO.for_shedule_master.month.MonthlyScheduleRequestDto;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface MasterScheduleManagementService {

    // === МЕСЯЧНОЕ РАСПИСАНИЕ ===
    MonthlyScheduleDto createMonthlySchedule(MonthlyScheduleRequestDto requestDto);
    MonthlyScheduleDto getMonthlySchedule(Long masterId, YearMonth month);
    void deleteMonthlySchedule(Long masterId, YearMonth month);

    // === НЕДЕЛЬНЫЕ КОРРЕКТИРОВКИ ===
    WeeklyAdjustmentDto createWeeklyAdjustment(WeeklyAdjustmentRequestDto requestDto);
    WeeklyAdjustmentDto getWeeklyAdjustment(Long masterId, LocalDate weekStartDate);
    void deleteWeeklyAdjustment(Long masterId, LocalDate weekStartDate);

    // === ДНЕВНЫЕ ИСКЛЮЧЕНИЯ ===
    DailyExceptionDto createDailyException(DailyExceptionRequestDto requestDto);
    List<DailyExceptionDto> getMonthlyExceptions(Long masterId, YearMonth month);
    void deleteDailyException(Long exceptionId);

    // === ПРОВЕРКА ДОСТУПНОСТИ (ОСНОВНАЯ ЛОГИКА) ===
    boolean isTimeSlotAvailable(Long masterId, LocalDate date, Integer timeSlot);
    List<Integer> getAvailableSlotsForDay(Long masterId, LocalDate date);

    // === МАССОВЫЕ ОПЕРАЦИИ ===
    List<MonthlyScheduleDto> createBulkMonthlySchedules(BulkScheduleRequestDto requestDto);
    MonthlyScheduleDto copyMonthSchedule(Long masterId, YearMonth sourceMonth, YearMonth targetMonth);
}
