package Frolov_back.NAILS_WEB_APP.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private SystemUser client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id", nullable = false)
    private SystemUser master;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private NailService service;

    @Column(nullable = false)
    private LocalDateTime appointmentDatetime;

    @Column(nullable = false)
    private LocalDateTime endDatetime;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatusType status;

    private String notes;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "appointment", fetch = FetchType.LAZY)
    private Review review;

    @OneToOne(mappedBy = "appointment", fetch = FetchType.LAZY)
    private Payment payment;

    @OneToMany(mappedBy = "appointment", fetch = FetchType.LAZY)
    private List<BonusTransaction> bonusTransactions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Конструкторы, геттеры, сеттеры
    public Appointment() {}
}

