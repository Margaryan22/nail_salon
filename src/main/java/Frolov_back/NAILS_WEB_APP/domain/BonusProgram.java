package Frolov_back.NAILS_WEB_APP.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
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
}