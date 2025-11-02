package Frolov_back.NAILS_WEB_APP.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "master_time_off")
public class MasterTimeOff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timeOffId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id", nullable = false)
    private SystemUser master;

    @Column(nullable = false)
    private LocalDateTime startDatetime;

    @Column(nullable = false)
    private LocalDateTime endDatetime;

    private String reason;

    // Конструкторы, геттеры, сеттеры
    public MasterTimeOff() {}
}