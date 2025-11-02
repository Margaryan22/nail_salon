package Frolov_back.NAILS_WEB_APP.domain;

import lombok.Data;

@Data
public class MasterServiceId implements java.io.Serializable {
    private Long master;
    private Long service;

    // Конструкторы, equals, hashCode
    public MasterServiceId() {}
}
