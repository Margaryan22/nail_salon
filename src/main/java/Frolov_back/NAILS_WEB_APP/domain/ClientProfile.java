package Frolov_back.NAILS_WEB_APP.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "client_profiles")
public class ClientProfile {
    @Id
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private SystemUser systemUser;

    private LocalDate birthdate;

    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer bonusPoints = 0;

    private String notes;

    // Конструкторы, геттеры, сеттеры
    public ClientProfile() {}

    public ClientProfile(SystemUser systemUser) {
        this.systemUser = systemUser;
    }

    // ... геттеры и сеттеры


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public SystemUser getSystemUser() {
        return systemUser;
    }

    public void setSystemUser(SystemUser systemUser) {
        this.systemUser = systemUser;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public Integer getBonusPoints() {
        return bonusPoints;
    }

    public void setBonusPoints(Integer bonusPoints) {
        this.bonusPoints = bonusPoints;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}