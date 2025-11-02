package Frolov_back.NAILS_WEB_APP.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "admin_profiles")
public class AdminProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private SystemUser systemUser;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 1")
    private Integer permissionsLevel = 1; // 1-обычный, 2-супер-админ

    // Конструкторы
    public AdminProfile() {}

    public AdminProfile(SystemUser systemUser, Integer permissionsLevel) {
        this.systemUser = systemUser;
        this.permissionsLevel = permissionsLevel;
    }

    // equals и hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdminProfile that = (AdminProfile) o;
        return adminId != null && adminId.equals(that.adminId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "AdminProfile{" +
                "adminId=" + adminId +
                ", permissionsLevel=" + permissionsLevel +
                '}';
    }
}