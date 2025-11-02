package Frolov_back.NAILS_WEB_APP.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Data
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
}