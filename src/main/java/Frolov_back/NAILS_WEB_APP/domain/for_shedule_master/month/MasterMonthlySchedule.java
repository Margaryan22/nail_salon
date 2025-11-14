package Frolov_back.NAILS_WEB_APP.domain.for_shedule_master.month;

import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "master_monthly_schedules")
public class MasterMonthlySchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long monthlyScheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id", nullable = false)
    private SystemUser master;

    @Column(nullable = false)
    private YearMonth scheduleMonth; // 2024-01

    @Column(nullable = false)
    private String templateType; // STANDARD, REDUCED, CUSTOM

    // Базовое расписание по умолчанию для месяца
    @OneToMany(mappedBy = "monthlySchedule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MonthlyScheduleSlot> monthlySlots = new ArrayList<>();

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

    public void addMonthlySlot(Integer dayOfWeek, String availableSlots) {
        MonthlyScheduleSlot slot = new MonthlyScheduleSlot(this, dayOfWeek, availableSlots);
        this.monthlySlots.add(slot);
    }

    public void clearMonthlySlots() {
        this.monthlySlots.clear();
    }
}
