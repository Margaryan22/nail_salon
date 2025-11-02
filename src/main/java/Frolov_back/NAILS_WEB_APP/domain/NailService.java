package Frolov_back.NAILS_WEB_APP.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "services")
public class NailService {
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

    //TODO: могут всплыть проблемы, т.к. у всего другого isActive - не забывай, что тут именно так
    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean active = true;

    @OneToMany(mappedBy = "service", fetch = FetchType.LAZY)
    private List<MasterServiceEntity> masterServices = new ArrayList<>();

    @OneToMany(mappedBy = "service", fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "service", fetch = FetchType.LAZY)
    private List<Promotion> promotions = new ArrayList<>();

    // Конструкторы, геттеры, сеттеры
    public NailService() {}
}