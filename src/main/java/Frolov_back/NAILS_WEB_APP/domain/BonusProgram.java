package Frolov_back.NAILS_WEB_APP.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bonus_programs")
public class BonusProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long programId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pointsPerAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal minAmount = BigDecimal.ZERO;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    // Конструкторы, геттеры, сеттеры
    public BonusProgram() {}

    // ... геттеры и сеттеры


    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPointsPerAmount() {
        return pointsPerAmount;
    }

    public void setPointsPerAmount(BigDecimal pointsPerAmount) {
        this.pointsPerAmount = pointsPerAmount;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}