package Frolov_back.NAILS_WEB_APP.service.DTO;

public class PasswordChangeRequestDto {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

    // Геттеры и сеттеры
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
