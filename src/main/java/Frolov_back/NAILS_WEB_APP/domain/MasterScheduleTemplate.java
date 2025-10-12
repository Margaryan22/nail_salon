package Frolov_back.NAILS_WEB_APP.domain;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "master_schedule_templates")
public class MasterScheduleTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id", nullable = false)
    private SystemUser master;

    @Column(nullable = false)
    private Short dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    // Конструкторы, геттеры, сеттеры
    public MasterScheduleTemplate() {}

    // ... геттеры и сеттеры

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public SystemUser getMaster() {
        return master;
    }

    public void setMaster(SystemUser master) {
        this.master = master;
    }

    public Short getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Short dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
