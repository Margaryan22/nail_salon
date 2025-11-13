package Frolov_back.NAILS_WEB_APP.service.for_shedule_master;

import Frolov_back.NAILS_WEB_APP.DTO.for_shedule_master.MasterWeeklyScheduleDto;
import Frolov_back.NAILS_WEB_APP.DTO.for_shedule_master.WeeklyScheduleRequestDto;


import java.time.LocalDate;
import java.util.List;

public interface MasterScheduleService {

    // Создать или обновить расписание на неделю
    MasterWeeklyScheduleDto createOrUpdateWeeklySchedule(WeeklyScheduleRequestDto requestDto);

    // Получить расписание мастера на определенную неделю
    MasterWeeklyScheduleDto getWeeklySchedule(Long masterId, LocalDate weekStartDate);

    // Получить текущее расписание мастера
    MasterWeeklyScheduleDto getCurrentWeeklySchedule(Long masterId);

    // Получить все расписания мастера
    List<MasterWeeklyScheduleDto> getAllMasterSchedules(Long masterId);

    // Удалить расписание на неделю
    void deleteWeeklySchedule(Long masterId, LocalDate weekStartDate);

    // Проверить доступность слота у мастера
    boolean isTimeSlotAvailable(Long masterId, LocalDate date, Integer timeSlot);

    // Получить доступные слоты на день
    List<Integer> getAvailableSlotsForDay(Long masterId, LocalDate date);
}