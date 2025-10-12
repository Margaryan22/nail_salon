package Frolov_back.NAILS_WEB_APP.domain;

public class MasterServiceId implements java.io.Serializable {
    private Long master;
    private Long service;

    // Конструкторы, equals, hashCode
    public MasterServiceId() {}

    // ... геттеры, сеттеры, equals, hashCode


    public Long getMaster() {
        return master;
    }

    public void setMaster(Long master) {
        this.master = master;
    }

    public Long getService() {
        return service;
    }

    public void setService(Long service) {
        this.service = service;
    }
}
