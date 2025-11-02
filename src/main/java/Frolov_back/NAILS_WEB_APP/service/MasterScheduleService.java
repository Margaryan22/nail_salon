package Frolov_back.NAILS_WEB_APP.service;

import Frolov_back.NAILS_WEB_APP.service.DTO.MasterScheduleDto;
import java.util.List;

public interface MasterScheduleService {
    List<MasterScheduleDto> getMasterSchedule(Long masterId);
    MasterScheduleDto createSchedule(MasterScheduleDto scheduleDto);
    MasterScheduleDto updateSchedule(Long scheduleId, MasterScheduleDto scheduleDto);
    void deleteSchedule(Long scheduleId);
    void setMasterSchedule(Long masterId, List<MasterScheduleDto> scheduleDtos);
}