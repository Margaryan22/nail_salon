package Frolov_back.NAILS_WEB_APP.service.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimeSlotDto {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean available;

    public TimeSlotDto(LocalDateTime startTime, LocalDateTime endTime, boolean available) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = available;
    }
}
