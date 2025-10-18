package Frolov_back.NAILS_WEB_APP.service.DTO;

public class LoginRequest {
    private String email;
    private String password;

    // Геттеры и сеттеры
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}