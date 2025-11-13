package Frolov_back.NAILS_WEB_APP.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AppointmentDto {
    private Long appointmentId;
    private Long clientId;
    private String clientName;
    private Long masterId;
    private String masterName;
    private Long serviceId;
    private String serviceName;
    private LocalDateTime appointmentDatetime;
    private LocalDateTime endDatetime;
    private BigDecimal price;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
}
