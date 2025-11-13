package Frolov_back.NAILS_WEB_APP.domain.for_shedule_master;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "master_schedule_slots")
public class MasterScheduleSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private MasterWeeklySchedule schedule;

    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    @Column(name = "available_slots", length = 100, nullable = false)
    private String availableSlots;

    // Конструкторы
    public MasterScheduleSlot() {}

    public MasterScheduleSlot(MasterWeeklySchedule schedule, Integer dayOfWeek, String availableSlots) {
        this.schedule = schedule;
        this.dayOfWeek = dayOfWeek;
        this.availableSlots = availableSlots;
    }
}