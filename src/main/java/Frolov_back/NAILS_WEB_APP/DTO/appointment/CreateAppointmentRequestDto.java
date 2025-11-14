package Frolov_back.NAILS_WEB_APP.DTO.appointment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Запрос на создание записи с использованием системы слотов")
public class CreateAppointmentRequestDto {

    @NotNull(message = "ID клиента обязателен")
    @Schema(description = "ID клиента", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long clientId;

    @NotNull(message = "ID мастера обязателен")
    @Schema(description = "ID мастера", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long masterId;

    @NotNull(message = "ID услуги обязателен")
    @Schema(description = "ID услуги", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long serviceId;

    @NotNull(message = "Дата записи обязательна")
    @FutureOrPresent(message = "Дата записи не может быть в прошлом")
    @Schema(description = "Дата записи", example = "2024-01-15", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate appointmentDate;

    @NotNull(message = "Временной слот обязателен")
    @Min(value = 1, message = "Номер слота должен быть от 1 до 10")
    @Max(value = 10, message = "Номер слота должен быть от 1 до 10")
    @Schema(description = "Номер временного слота (1-10)", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer timeSlot;

    @Schema(description = "Дополнительные заметки", example = "Предпочтительно у окна")
    private String notes;
}
