package Frolov_back.NAILS_WEB_APP.DTO.appointment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimeSlotDto {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean available;
    private Integer slotNumber; // Добавляем номер слота

    public TimeSlotDto(LocalDateTime startTime, LocalDateTime endTime, boolean available) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = available;
    }

    public TimeSlotDto(LocalDateTime startTime, LocalDateTime endTime, boolean available, Integer slotNumber) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = available;
        this.slotNumber = slotNumber;
    }
}
