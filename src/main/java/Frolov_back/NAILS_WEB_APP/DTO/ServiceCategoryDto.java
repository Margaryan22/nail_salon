package Frolov_back.NAILS_WEB_APP.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
@Schema(description = "DTO для категории услуг")
public class ServiceCategoryDto {
    private Long categoryId;

    @NotBlank(message = "Название категории обязательно")
    @Schema(description = "Название категории", example = "Маникюр", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Описание категории", example = "Услуги по уходу за ногтями")
    private String description;

    @Schema(description = "Порядок сортировки", example = "1")
    private Integer sortOrder = 0;
}