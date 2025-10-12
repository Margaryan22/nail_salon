package Frolov_back.NAILS_WEB_APP.service.DTO;

import java.time.LocalDateTime;

public class UserWithProfileDto {
    // Основная информация пользователя
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private LocalDateTime createdAt;
    private String role;

    // Профиль (только один будет заполнен)
    private AdminProfileDto adminProfile;
    private ClientProfileDto clientProfile;
    private MasterProfileDto masterProfile;

    // Геттеры и сеттеры
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public AdminProfileDto getAdminProfile() { return adminProfile; }
    public void setAdminProfile(AdminProfileDto adminProfile) { this.adminProfile = adminProfile; }
    public ClientProfileDto getClientProfile() { return clientProfile; }
    public void setClientProfile(ClientProfileDto clientProfile) { this.clientProfile = clientProfile; }
    public MasterProfileDto getMasterProfile() { return masterProfile; }
    public void setMasterProfile(MasterProfileDto masterProfile) { this.masterProfile = masterProfile; }
}