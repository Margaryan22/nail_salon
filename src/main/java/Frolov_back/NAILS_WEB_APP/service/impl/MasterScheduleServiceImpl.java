package Frolov_back.NAILS_WEB_APP.service.impl;

import Frolov_back.NAILS_WEB_APP.domain.MasterScheduleTemplate;
import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import Frolov_back.NAILS_WEB_APP.repository.MasterScheduleTemplateRepository;
import Frolov_back.NAILS_WEB_APP.repository.SystemUserRepository;
import Frolov_back.NAILS_WEB_APP.service.MasterScheduleService;
import Frolov_back.NAILS_WEB_APP.service.DTO.MasterScheduleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MasterScheduleServiceImpl implements MasterScheduleService {

    private final MasterScheduleTemplateRepository scheduleRepository;
    private final SystemUserRepository systemUserRepository;

    @Override
    public List<MasterScheduleDto> getMasterSchedule(Long masterId) {
        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        return scheduleRepository.findByMasterOrderByDayOfWeekAscStartTimeAsc(master).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional
    public MasterScheduleDto createSchedule(MasterScheduleDto scheduleDto) {
        SystemUser master = systemUserRepository.findById(scheduleDto.getMasterId())
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        MasterScheduleTemplate schedule = new MasterScheduleTemplate();
        schedule.setMaster(master);
        schedule.setDayOfWeek(scheduleDto.getDayOfWeek());
        schedule.setStartTime(scheduleDto.getStartTime());
        schedule.setEndTime(scheduleDto.getEndTime());

        MasterScheduleTemplate saved = scheduleRepository.save(schedule);
        return convertToDto(saved);
    }

    @Override
    @Transactional
    public void setMasterSchedule(Long masterId, List<MasterScheduleDto> scheduleDtos) {
        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        // Удаляем старое расписание
        scheduleRepository.deleteByMaster(master);

        // Создаем новое расписание
        for (MasterScheduleDto dto : scheduleDtos) {
            MasterScheduleTemplate schedule = new MasterScheduleTemplate();
            schedule.setMaster(master);
            schedule.setDayOfWeek(dto.getDayOfWeek());
            schedule.setStartTime(dto.getStartTime());
            schedule.setEndTime(dto.getEndTime());
            scheduleRepository.save(schedule);
        }
    }

    @Override
    @Transactional
    public MasterScheduleDto updateSchedule(Long scheduleId, MasterScheduleDto scheduleDto) {
        MasterScheduleTemplate schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Расписание не найдено"));

        schedule.setDayOfWeek(scheduleDto.getDayOfWeek());
        schedule.setStartTime(scheduleDto.getStartTime());
        schedule.setEndTime(scheduleDto.getEndTime());

        MasterScheduleTemplate updated = scheduleRepository.save(schedule);
        return convertToDto(updated);
    }

    @Override
    @Transactional
    public void deleteSchedule(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }

    private MasterScheduleDto convertToDto(MasterScheduleTemplate schedule) {
        MasterScheduleDto dto = new MasterScheduleDto();
        dto.setScheduleId(schedule.getScheduleId());
        dto.setMasterId(schedule.getMaster().getUserId());
        dto.setDayOfWeek(schedule.getDayOfWeek());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        return dto;
    }
}
