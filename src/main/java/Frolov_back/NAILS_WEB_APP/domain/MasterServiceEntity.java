package Frolov_back.NAILS_WEB_APP.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
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
    private Service service;

    @Column(precision = 10, scale = 2)
    private BigDecimal masterPrice;

    // Конструкторы, геттеры, сеттеры
    public MasterServiceEntity() {}

    public MasterServiceEntity(SystemUser master, Service service, BigDecimal masterPrice) {
        this.master = master;
        this.service = service;
        this.masterPrice = masterPrice;
    }

    // ... геттеры и сеттеры


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

    public BigDecimal getMasterPrice() {
        return masterPrice;
    }

    public void setMasterPrice(BigDecimal masterPrice) {
        this.masterPrice = masterPrice;
    }
}

