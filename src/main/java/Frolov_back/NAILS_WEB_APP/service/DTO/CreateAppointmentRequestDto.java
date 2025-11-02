package Frolov_back.NAILS_WEB_APP.service.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateAppointmentRequestDto {
    private Long clientId;
    private Long masterId;
    private Long serviceId;
    private LocalDateTime appointmentDateTime;
    private String notes;
}
