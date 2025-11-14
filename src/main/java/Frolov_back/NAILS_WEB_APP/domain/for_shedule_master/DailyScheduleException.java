package Frolov_back.NAILS_WEB_APP.domain.for_shedule_master;

import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "daily_schedule_exceptions")
public class DailyScheduleException {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exceptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id", nullable = false)
    private SystemUser master;

    @Column(nullable = false)
    private LocalDate exceptionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleExceptionType exceptionType;

    @Column(nullable = false)
    private Boolean allDay = true; // Весь день выходной

    @Column(length = 500)
    private String reason;

    // Если не весь день выходной, то какие слоты доступны
    @Column(name = "available_slots", length = 100)
    private String availableSlots;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
