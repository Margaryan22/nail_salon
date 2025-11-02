package Frolov_back.NAILS_WEB_APP.service.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MasterServiceResponseDto {
    private Long masterId;
    private String masterName;
    private Long serviceId;
    private String serviceName;
    private BigDecimal masterPrice;
}
