package Frolov_back.NAILS_WEB_APP.domain.for_shedule_master;

import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Data
@Table(name = "master_weekly_schedules")
public class MasterWeeklySchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id", nullable = false)
    private SystemUser master;

    @Column(nullable = false)
    private LocalDate weekStartDate;

    // СВЯЗЬ С ОТДЕЛЬНЫМИ СЛОТАМИ
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MasterScheduleSlot> scheduleSlots = new ArrayList<>();

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // МЕТОД ДЛЯ ДОБАВЛЕНИЯ СЛОТОВ
    public void addScheduleSlot(Integer dayOfWeek, String availableSlots) {
        MasterScheduleSlot slot = new MasterScheduleSlot(this, dayOfWeek, availableSlots);
        this.scheduleSlots.add(slot);
    }

    // МЕТОД ДЛЯ ОЧИСТКИ СЛОТОВ
    public void clearScheduleSlots() {
        this.scheduleSlots.clear();
    }
}