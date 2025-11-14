package Frolov_back.NAILS_WEB_APP.domain.for_shedule_master;

import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "weekly_schedule_adjustments")
public class WeeklyScheduleAdjustment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adjustmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id", nullable = false)
    private SystemUser master;

    @Column(nullable = false)
    private LocalDate weekStartDate;

    @Column(length = 500)
    private String reason; // Причина корректировки

    @OneToMany(mappedBy = "adjustment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AdjustmentSlot> adjustmentSlots = new ArrayList<>();

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void addAdjustmentSlot(Integer dayOfWeek, String availableSlots) {
        AdjustmentSlot slot = new AdjustmentSlot(this, dayOfWeek, availableSlots);
        this.adjustmentSlots.add(slot);
    }
}