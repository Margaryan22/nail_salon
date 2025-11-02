package Frolov_back.NAILS_WEB_APP.service.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MasterServiceRequestDto {
    private Long masterId;
    private Long serviceId;
    private BigDecimal masterPrice;
}