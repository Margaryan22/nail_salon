package Frolov_back.NAILS_WEB_APP.domain.for_shedule_master;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "adjustment_slots")
public class AdjustmentSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adjustment_id", nullable = false)
    private WeeklyScheduleAdjustment adjustment;

    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    @Column(name = "available_slots", length = 100, nullable = false)
    private String availableSlots;

    public AdjustmentSlot() {}

    public AdjustmentSlot(WeeklyScheduleAdjustment adjustment, Integer dayOfWeek, String availableSlots) {
        this.adjustment = adjustment;
        this.dayOfWeek = dayOfWeek;
        this.availableSlots = availableSlots;
    }
}
