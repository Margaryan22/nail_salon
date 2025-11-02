package Frolov_back.NAILS_WEB_APP.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "master_profiles")
public class MasterProfile {
    @Id
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private SystemUser systemUser;

    private String specialization;
    private Integer workExperience;
    private String description;
    private String photoUrl;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    // Конструкторы, геттеры, сеттеры
    public MasterProfile() {}

    public MasterProfile(SystemUser systemUser) {
        this.systemUser = systemUser;
    }
}