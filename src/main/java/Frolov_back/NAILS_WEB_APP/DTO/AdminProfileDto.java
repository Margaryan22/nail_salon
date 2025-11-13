package Frolov_back.NAILS_WEB_APP.DTO;

import lombok.Data;

@Data
public class AdminProfileDto {
    private Long adminId;
    private Long userId;
    private Integer permissionsLevel;
}
