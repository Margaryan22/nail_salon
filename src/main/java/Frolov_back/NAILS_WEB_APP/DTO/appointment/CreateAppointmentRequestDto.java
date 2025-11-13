package Frolov_back.NAILS_WEB_APP.DTO.appointment;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CreateAppointmentRequestDto {
    private Long clientId;
    private Long masterId;
    private Long serviceId;
    private LocalDate appointmentDate;  // Изменяем на LocalDate
    private Integer timeSlot;           // Добавляем номер слота (1-10)
    private String notes;
}
