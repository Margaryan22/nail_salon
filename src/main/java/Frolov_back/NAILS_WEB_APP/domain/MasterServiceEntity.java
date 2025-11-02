package Frolov_back.NAILS_WEB_APP.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "master_services")
@IdClass(MasterServiceId.class)
public class MasterServiceEntity {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id", nullable = false)
    private SystemUser master;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private NailService service;

    @Column(precision = 10, scale = 2)
    private BigDecimal masterPrice;

    // Конструкторы, геттеры, сеттеры
    public MasterServiceEntity() {}

    public MasterServiceEntity(SystemUser master, NailService service, BigDecimal masterPrice) {
        this.master = master;
        this.service = service;
        this.masterPrice = masterPrice;
    }
}

