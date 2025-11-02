package Frolov_back.NAILS_WEB_APP.service.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NailServiceDto {
    private Long serviceId;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private Integer baseDuration;
    private BigDecimal basePrice;
    private Boolean Active;
}
