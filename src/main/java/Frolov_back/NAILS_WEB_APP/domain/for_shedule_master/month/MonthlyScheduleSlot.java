package Frolov_back.NAILS_WEB_APP.domain.for_shedule_master.month;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "monthly_schedule_slots")
public class MonthlyScheduleSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monthly_schedule_id", nullable = false)
    private MasterMonthlySchedule monthlySchedule;

    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    @Column(name = "available_slots", length = 100, nullable = false)
    private String availableSlots;

    public MonthlyScheduleSlot() {}

    public MonthlyScheduleSlot(MasterMonthlySchedule monthlySchedule, Integer dayOfWeek, String availableSlots) {
        this.monthlySchedule = monthlySchedule;
        this.dayOfWeek = dayOfWeek;
        this.availableSlots = availableSlots;
    }
}
