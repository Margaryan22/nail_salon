package Frolov_back.NAILS_WEB_APP.domain;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "system_users")
public class SystemUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoleType role;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    // Связи
    @OneToOne(mappedBy = "systemUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ClientProfile clientProfile;

    @OneToOne(mappedBy = "systemUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MasterProfile masterProfile;

    @OneToOne(mappedBy = "systemUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private AdminProfile adminProfile;

    // Для мастеров - услуги которые они предоставляют
    @OneToMany(mappedBy = "master", fetch = FetchType.LAZY)
    private List<MasterServiceEntity> masterServices = new ArrayList<>();

    // Записи клиента
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<Appointment> clientAppointments = new ArrayList<>();

    // Записи мастера
    @OneToMany(mappedBy = "master", fetch = FetchType.LAZY)
    private List<Appointment> masterAppointments = new ArrayList<>();

    // Отзывы о мастере
    @OneToMany(mappedBy = "master", fetch = FetchType.LAZY)
    private List<Review> masterReviews = new ArrayList<>();

    // Отзывы клиента
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<Review> clientReviews = new ArrayList<>();

    // Бонусные транзакции клиента
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<BonusTransaction> bonusTransactions = new ArrayList<>();

    // Расписание мастера
    @OneToMany(mappedBy = "master", fetch = FetchType.LAZY)
    private List<MasterScheduleTemplate> scheduleTemplates = new ArrayList<>();

    // Выходные мастера
    @OneToMany(mappedBy = "master", fetch = FetchType.LAZY)
    private List<MasterTimeOff> timeOffs = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Конструкторы, геттеры, сеттеры
    public SystemUser() {}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash; // Spring Security будет использовать это поле
    }

    @Override
    public String getUsername() {
        return email; // Spring Security будет использовать email как username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // ... геттеры и сеттеры

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRoleType getRole() {
        return role;
    }

    public void setRole(UserRoleType role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ClientProfile getClientProfile() {
        return clientProfile;
    }

    public void setClientProfile(ClientProfile clientProfile) {
        this.clientProfile = clientProfile;
    }

    public MasterProfile getMasterProfile() { return masterProfile; }

    public void setMasterProfile(MasterProfile masterProfile) {
        this.masterProfile = masterProfile;
    }

    public List<MasterServiceEntity> getMasterServicesMasterServiceEntity() {
        return masterServices;
    }

    public void setMasterServicesMasterServiceEntity(List<MasterServiceEntity> masterServicesEntity) {
        this.masterServices = masterServicesEntity;
    }

    public List<Appointment> getClientAppointments() {
        return clientAppointments;
    }

    public void setClientAppointments(List<Appointment> clientAppointments) {
        this.clientAppointments = clientAppointments;
    }

    public List<Appointment> getMasterAppointments() {
        return masterAppointments;
    }

    public void setMasterAppointments(List<Appointment> masterAppointments) {
        this.masterAppointments = masterAppointments;
    }

    public List<Review> getMasterReviews() {
        return masterReviews;
    }

    public void setMasterReviews(List<Review> masterReviews) {
        this.masterReviews = masterReviews;
    }

    public List<Review> getClientReviews() {
        return clientReviews;
    }

    public void setClientReviews(List<Review> clientReviews) {
        this.clientReviews = clientReviews;
    }

    public List<BonusTransaction> getBonusTransactions() {
        return bonusTransactions;
    }

    public void setBonusTransactions(List<BonusTransaction> bonusTransactions) {
        this.bonusTransactions = bonusTransactions;
    }

    public List<MasterScheduleTemplate> getScheduleTemplates() {
        return scheduleTemplates;
    }

    public void setScheduleTemplates(List<MasterScheduleTemplate> scheduleTemplates) {
        this.scheduleTemplates = scheduleTemplates;
    }

    public List<MasterTimeOff> getTimeOffs() {
        return timeOffs;
    }

    public void setTimeOffs(List<MasterTimeOff> timeOffs) {
        this.timeOffs = timeOffs;
    }

    public AdminProfile getAdminProfile() {
        return adminProfile;
    }

    public void setAdminProfile(AdminProfile adminProfile) {
        if (adminProfile != null) {
            adminProfile.setSystemUser(this);
        }
        this.adminProfile = adminProfile;
    }
}

