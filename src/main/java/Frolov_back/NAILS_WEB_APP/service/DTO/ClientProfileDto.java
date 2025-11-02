package Frolov_back.NAILS_WEB_APP.service.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ClientProfileDto {
    private Long userId;
    private LocalDate birthdate;
    private Integer bonusPoints;
    private String notes;
}