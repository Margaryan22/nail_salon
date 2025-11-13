package Frolov_back.NAILS_WEB_APP.DTO;

import lombok.Data;

@Data
public class MasterProfileDto {
    private Long userId;
    private String specialization;
    private Integer workExperience;
    private String description;
    private String photoUrl;
    private Boolean isActive;
}