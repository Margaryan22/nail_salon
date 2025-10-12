package Frolov_back.NAILS_WEB_APP.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
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

    // ... геттеры и сеттеры


    public Long getTimeOffId() {
        return timeOffId;
    }

    public void setTimeOffId(Long timeOffId) {
        this.timeOffId = timeOffId;
    }

    public SystemUser getMaster() {
        return master;
    }

    public void setMaster(SystemUser master) {
        this.master = master;
    }

    public LocalDateTime getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(LocalDateTime startDatetime) {
        this.startDatetime = startDatetime;
    }

    public LocalDateTime getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(LocalDateTime endDatetime) {
        this.endDatetime = endDatetime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}