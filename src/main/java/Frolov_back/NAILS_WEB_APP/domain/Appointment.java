package Frolov_back.NAILS_WEB_APP.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Entity
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
    private Service service;

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

    // ... геттеры и сеттеры


    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public SystemUser getClient() {
        return client;
    }

    public void setClient(SystemUser client) {
        this.client = client;
    }

    public SystemUser getMaster() {
        return master;
    }

    public void setMaster(SystemUser master) {
        this.master = master;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public LocalDateTime getAppointmentDatetime() {
        return appointmentDatetime;
    }

    public void setAppointmentDatetime(LocalDateTime appointmentDatetime) {
        this.appointmentDatetime = appointmentDatetime;
    }

    public LocalDateTime getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(LocalDateTime endDatetime) {
        this.endDatetime = endDatetime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public AppointmentStatusType getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatusType status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public List<BonusTransaction> getBonusTransactions() {
        return bonusTransactions;
    }

    public void setBonusTransactions(List<BonusTransaction> bonusTransactions) {
        this.bonusTransactions = bonusTransactions;
    }
}

