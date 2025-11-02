package Frolov_back.NAILS_WEB_APP.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "services")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ServiceCategory category;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Integer baseDuration;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "service", fetch = FetchType.LAZY)
    private List<MasterServiceEntity> masterServices = new ArrayList<>();

    @OneToMany(mappedBy = "service", fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "service", fetch = FetchType.LAZY)
    private List<Promotion> promotions = new ArrayList<>();

    // Конструкторы, геттеры, сеттеры
    public Service() {}
}